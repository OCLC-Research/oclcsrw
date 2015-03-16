<img src='http://www.oclc.org/common/images/logos/oclc/OCLC_TM_V_SM.jpg' />

# Introduction #

The **SRW** (Search & Retrieve Web Service) initiative is part of an international collaborative effort to develop a standard web-based text-searching interface. It draws heavily on the abstract models and functionality of Z39.50, but removes much of the complexity. SRW is built using common web development tools (WSDL, SOAP, HTTP and XML) and development of SRW interfaces to data repositories is significantly easier than for Z39.50. In addition, such arcane record formats as MARC and GRS-1 have been replaced with XML.

**SRU** (Search & Retrieve URL Service) is a URL-based alternative to SRW. Messages are sent via HTTP using the GET method and the components of the SRW SOAP request are mapped to simple HTTP parameters. The response to an SRU request is identical to the response to an SRW request, with the SOAP wrapper removed.

Complete information on the SRW/SRU standard can be found at the maintenance agency web site: http://www.loc.gov/standards/sru/

# Details #

[InstallationInstructions](InstallationInstructions.md)
[ConfigurationFiles](ConfigurationFiles.md)
[WriteYourOwnDatabaseInterface](WriteYourOwnDatabaseInterface.md)