package iss.bugproject.domain;


public class File extends Entity<Integer> {
    private Integer id;
    private String path;
    private String descriere;
    private Integer bugId;

    public File(String path, String descriere, Integer bugId) {
        this.path = path;
        this.descriere = descriere;
        this.bugId = bugId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public String getDescriere() {
        return descriere;
    }

    public Integer getBugId() {
        return bugId;
    }

    @Override
    public String toString() {
        return "BugFile{" +
                "id=" + getId() +
                ", path='" + path + '\'' +
                ", descriere='" + descriere + '\'' +
                ", bugId=" + bugId +
                '}';
    }
}

