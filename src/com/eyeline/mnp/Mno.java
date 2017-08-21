package com.eyeline.mnp;

/**
 * @author Chukanov
 */
public class Mno {

    private String id;
    private String country;
    private String title;
    private String area;

    public Mno(String id, String country, String title, String area) {
        this.id = id;
        this.country = country;
        this.title = title;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArea() {
        return area;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Mno{" +
                "id='" + id + '\'' +
                ", country='" + country + '\'' +
                ", title='" + title + '\'' +
                ", area='" + area + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mno mno = (Mno) o;
        return id.equals(mno.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
