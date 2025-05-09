package iss.bugproject.domain;

public class Bug extends Entity<Integer> {
    private Integer id;
    private String denumire;
    private String descriere;
    private String status;
    private Integer idProgramator;

    public Bug(String denumire, String descriere, String status, Integer idProgramator) {
        this.denumire = denumire;
        this.descriere = descriere;
        this.status = status;
        this.idProgramator = idProgramator;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDenumire() {
        return denumire;
    }

    public String getDescriere() {
        return descriere;
    }

    public String getStatus() {
        return status;
    }

    public Integer getIdProgramator() {
        return idProgramator;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "id=" + getId() +
                ", denumire='" + denumire + '\'' +
                ", descriere='" + descriere + '\'' +
                ", status='" + status + '\'' +
                ", idProgramator=" + idProgramator +
                '}';
    }
}
