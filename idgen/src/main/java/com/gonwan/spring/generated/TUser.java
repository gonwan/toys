package com.gonwan.spring.generated;

import javax.persistence.*;

@Entity
@Table(name = "t_user", schema = "test", catalog = "")
public class TUser {
    private int id;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;
    private String field9;
    private String field10;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /* mysql / table */
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGenerator")
    //@TableGenerator(name = "tableGenerator", allocationSize = 100, table = "t_generator", pkColumnName = "gen_name", valueColumnName = "gen_value", pkColumnValue = "SEQ_USER")
    /* mariadb / sequence  */
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    //@SequenceGenerator(name = "sequenceGenerator", allocationSize = 100, sequenceName = "s_user")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "field1", nullable = true, length = 255)
    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    @Basic
    @Column(name = "field2", nullable = true, length = 255)
    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Basic
    @Column(name = "field3", nullable = true, length = 255)
    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    @Basic
    @Column(name = "field4", nullable = true, length = 255)
    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    @Basic
    @Column(name = "field5", nullable = true, length = 255)
    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    @Basic
    @Column(name = "field6", nullable = true, length = 255)
    public String getField6() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6 = field6;
    }

    @Basic
    @Column(name = "field7", nullable = true, length = 255)
    public String getField7() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7 = field7;
    }

    @Basic
    @Column(name = "field8", nullable = true, length = 255)
    public String getField8() {
        return field8;
    }

    public void setField8(String field8) {
        this.field8 = field8;
    }

    @Basic
    @Column(name = "field9", nullable = true, length = 255)
    public String getField9() {
        return field9;
    }

    public void setField9(String field9) {
        this.field9 = field9;
    }

    @Basic
    @Column(name = "field10", nullable = true, length = 255)
    public String getField10() {
        return field10;
    }

    public void setField10(String field10) {
        this.field10 = field10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TUser tUser = (TUser) o;

        if (id != tUser.id) return false;
        if (field1 != null ? !field1.equals(tUser.field1) : tUser.field1 != null) return false;
        if (field2 != null ? !field2.equals(tUser.field2) : tUser.field2 != null) return false;
        if (field3 != null ? !field3.equals(tUser.field3) : tUser.field3 != null) return false;
        if (field4 != null ? !field4.equals(tUser.field4) : tUser.field4 != null) return false;
        if (field5 != null ? !field5.equals(tUser.field5) : tUser.field5 != null) return false;
        if (field6 != null ? !field6.equals(tUser.field6) : tUser.field6 != null) return false;
        if (field7 != null ? !field7.equals(tUser.field7) : tUser.field7 != null) return false;
        if (field8 != null ? !field8.equals(tUser.field8) : tUser.field8 != null) return false;
        if (field9 != null ? !field9.equals(tUser.field9) : tUser.field9 != null) return false;
        if (field10 != null ? !field10.equals(tUser.field10) : tUser.field10 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (field1 != null ? field1.hashCode() : 0);
        result = 31 * result + (field2 != null ? field2.hashCode() : 0);
        result = 31 * result + (field3 != null ? field3.hashCode() : 0);
        result = 31 * result + (field4 != null ? field4.hashCode() : 0);
        result = 31 * result + (field5 != null ? field5.hashCode() : 0);
        result = 31 * result + (field6 != null ? field6.hashCode() : 0);
        result = 31 * result + (field7 != null ? field7.hashCode() : 0);
        result = 31 * result + (field8 != null ? field8.hashCode() : 0);
        result = 31 * result + (field9 != null ? field9.hashCode() : 0);
        result = 31 * result + (field10 != null ? field10.hashCode() : 0);
        return result;
    }
}
