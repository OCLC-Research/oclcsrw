package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContentTypeNegotiator {
    protected static Log log=LogFactory.getLog(ContentTypeNegotiator.class.getName());

    private final List<VariantSpec> variantSpecs = new ArrayList<VariantSpec>();
    private List<MediaRangeSpec> defaultAcceptRanges = 
            Collections.singletonList(MediaRangeSpec.parseRange("*/*"));
    private final Collection<AcceptHeaderOverride> userAgentOverrides = new ArrayList<AcceptHeaderOverride>();

    public VariantSpec addVariant(String mediaType) {
        VariantSpec result = new VariantSpec(mediaType);
        variantSpecs.add(result);
        return result;
    }

    /**
     * Sets an Accept header to be used as the default if a client does
     * not send an Accept header, or if the Accept header cannot be parsed.
     * Defaults to "* / *".
     */
    public void setDefaultAccept(String accept) {
        this.defaultAcceptRanges = MediaRangeSpec.parseAccept(accept);
    }

    /**
     * Overrides the Accept header for certain user agents. This can be
     * used to implement special-case handling for user agents that send
     * faulty Accept headers. 
     * @param userAgentString A pattern to be matched against the User-Agent header;
     * 		<tt>null</tt> means regardless of User-Agent
     * @param originalAcceptHeader Only override the Accept header if the user agent
     * 		sends this header; <tt>null</tt> means always override  
     * @param newAcceptHeader The Accept header to be used instead
     */
    public void addUserAgentOverride(Pattern userAgentString, 
      String originalAcceptHeader, String newAcceptHeader) {
        this.userAgentOverrides.add(new AcceptHeaderOverride(
            userAgentString, originalAcceptHeader, newAcceptHeader));
    }

    public MediaRangeSpec getBestMatch(String accept) {
        return getBestMatch(accept, null);
    }

    public MediaRangeSpec getBestMatch(String accept, String userAgent) {
        if(log.isDebugEnabled())
            log.debug("accept="+accept+", userAgent="+userAgent);
        if (userAgent == null)
            userAgent = "";
        AcceptHeaderOverride override;
        Iterator<AcceptHeaderOverride> it = userAgentOverrides.iterator();
        String overriddenAccept = accept;
        while (it.hasNext()) {
            override = it.next();
            if (override.matches(accept, userAgent)) {
                overriddenAccept = override.getReplacement();
            }
        }
        return new Negotiation(toAcceptRanges(overriddenAccept)).negotiate();
    }

    private List<MediaRangeSpec> toAcceptRanges(String accept) {
        if (accept == null) {
            return defaultAcceptRanges;
        }
        List<MediaRangeSpec> result = MediaRangeSpec.parseAccept(accept);
        if (result.isEmpty()) {
            return defaultAcceptRanges;
        }
        return result;
    }

    public class VariantSpec {
        private final MediaRangeSpec type;
        private final List<MediaRangeSpec> aliases = new ArrayList<MediaRangeSpec>();
        private boolean isDefault = false;
        public VariantSpec(String mediaType) {
            type = MediaRangeSpec.parseType(mediaType);
        }
        public VariantSpec addAliasMediaType(String mediaType) {
            MediaRangeSpec alias = MediaRangeSpec.parseType(mediaType);
            if(!mediaType.contains("q=")) // if no quality measure specified, inherit from the base
                alias.setQuality(type.getQuality());
            aliases.add(alias);
            return this;
        }
        public void makeDefault() {
            isDefault = true;
        }
        public MediaRangeSpec getMediaType() {
            return type;
        }
        public boolean isDefault() {
            return isDefault;
        }
        public List<MediaRangeSpec> getAliases() {
            return aliases;
        }
    }

    private class Negotiation {
        private final List<MediaRangeSpec> ranges;
        private MediaRangeSpec bestMatchingVariant = null;
        private MediaRangeSpec bestDefaultVariant = null;
        private double bestMatchingQuality = 0;
        private double bestDefaultQuality = 0;

        Negotiation(List<MediaRangeSpec> ranges) {
            this.ranges = ranges;
            if(log.isDebugEnabled())
                log.debug("ranges: "+ranges);
        }

        MediaRangeSpec negotiate() {
            Iterator<VariantSpec> it = variantSpecs.iterator();
            VariantSpec variant;
            while (it.hasNext()) {
                variant = it.next();
                if (variant.isDefault) {
                    evaluateDefaultVariant(variant.getMediaType());
                }
                evaluateVariant(variant.getMediaType());
                Iterator<MediaRangeSpec> aliasIt = variant.getAliases().iterator();
                while (aliasIt.hasNext())
                    evaluateVariantAlias(aliasIt.next(), variant.getMediaType());
            }
            if(log.isInfoEnabled())
                log.info("bestMatchingVariant="+bestMatchingVariant+", bestDefaultVariant="+bestDefaultVariant);
            return (bestMatchingVariant == null) ? bestDefaultVariant : bestMatchingVariant;
        }

        private void evaluateVariantAlias(MediaRangeSpec variant, MediaRangeSpec isAliasFor) {
            if (variant.getBestMatch(ranges) == null) return;
            double q = variant.getBestMatch(ranges).getQuality();
            if (q * variant.getQuality() > bestMatchingQuality) {
                bestMatchingVariant = isAliasFor;
                bestMatchingQuality = q * variant.getQuality();
            }
        }

        private void evaluateVariant(MediaRangeSpec variant) {
            evaluateVariantAlias(variant, variant);
        }

        private void evaluateDefaultVariant(MediaRangeSpec variant) {
            if (variant.getQuality() > bestDefaultQuality) {
                bestDefaultVariant = variant;
                bestDefaultQuality = 0.00001 * variant.getQuality();
            }
        }
    }

    private class AcceptHeaderOverride {
        private final Pattern userAgentPattern;
        private final String original;
        private final String replacement;
        AcceptHeaderOverride(Pattern userAgentPattern, String original, String replacement) {
            this.userAgentPattern = userAgentPattern;
            this.original = original;
            this.replacement = replacement;
        }
        boolean matches(String acceptHeader) {
            return matches(acceptHeader, null);
        }
        boolean matches(String acceptHeader, String userAgentHeader) {
            return (userAgentPattern == null 
                 || userAgentPattern.matcher(userAgentHeader).find()) 
                 && (original == null || original.equals(acceptHeader));
        }
        String getReplacement() {
                return replacement;
        }
    }
}