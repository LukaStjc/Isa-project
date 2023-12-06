package rs.ac.uns.ftn.informatika.jpa.dto;


import rs.ac.uns.ftn.informatika.jpa.model.Complaint;

public class ComplaintDTO {
    private Integer id;
    private String comment;
    private String writerName;
    private String companyName;
    private String companyAdminName;
    private String reply;

    ComplaintDTO(){}

    public ComplaintDTO(Integer id, String comment, String writerName, String companyName, String companyAdminName) {
        this.id = id;
        this.comment = comment;
        this.writerName = writerName;
        this.companyName = companyName;
        this.companyAdminName = companyAdminName;
    }

    public ComplaintDTO(Complaint complaint) {
        this.id = complaint.getId();
        this.comment = complaint.getComment();
        this.writerName = complaint.getRegisteredUser().getFirstName();
        if(complaint.getCompany() != null) this.companyName = complaint.getCompany().getName();
        if(complaint.getCompanyAdmin() != null) this.companyAdminName = complaint.getCompanyAdmin().getFirstName();
    }

    public Integer getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getWriterName() {
        return writerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAdminName() {
        return companyAdminName;
    }

    public String getReply() {
        return reply;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyAdminName(String companyAdminName) {
        this.companyAdminName = companyAdminName;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
