/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

/**
 *
 * @author levan
 */
public class RecordMetadata {
    private String user;
    private String comment;
    private String workflowStatus;

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the workflowStatus
     */
    public String getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @param workflowStatus the workflowStatus to set
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
}
