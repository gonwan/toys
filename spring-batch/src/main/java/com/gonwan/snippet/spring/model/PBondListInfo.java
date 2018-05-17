package com.gonwan.snippet.spring.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "p_bond_list_info", schema = "test", catalog = "")
public class PBondListInfo {
    private String id;
    private String delflag;
    private Timestamp createDate;
    private Timestamp modifyDate;
    private String username;
    private String inputer;
    private String checker;
    private String sts;
    private String bondKey;
    private String listedMarket;
    private String bondId;
    private String shortName;
    private String isCrossMkt;
    private String isMortgage;
    private Integer listedDate;
    private Integer delistedDate;
    private String mktType;
    private String pinyin;
    private String pinyinFull;
    private String shortNameEn;

    @Id
    @Column(name = "ID", nullable = false, length = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "delflag", nullable = true, length = 1)
    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }

    @Basic
    @Column(name = "create_date", nullable = true)
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "modify_date", nullable = true)
    public Timestamp getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Basic
    @Column(name = "username", nullable = true, length = 32)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "inputer", nullable = true, length = 32)
    public String getInputer() {
        return inputer;
    }

    public void setInputer(String inputer) {
        this.inputer = inputer;
    }

    @Basic
    @Column(name = "checker", nullable = true, length = 32)
    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    @Basic
    @Column(name = "sts", nullable = true, length = 1)
    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }

    @Basic
    @Column(name = "Bond_Key", nullable = true, length = 25)
    public String getBondKey() {
        return bondKey;
    }

    public void setBondKey(String bondKey) {
        this.bondKey = bondKey;
    }

    @Basic
    @Column(name = "Listed_Market", nullable = true, length = 3)
    public String getListedMarket() {
        return listedMarket;
    }

    public void setListedMarket(String listedMarket) {
        this.listedMarket = listedMarket;
    }

    @Basic
    @Column(name = "Bond_ID", nullable = true, length = 32)
    public String getBondId() {
        return bondId;
    }

    public void setBondId(String bondId) {
        this.bondId = bondId;
    }

    @Basic
    @Column(name = "Short_Name", nullable = true, length = 32)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Basic
    @Column(name = "Is_Cross_Mkt", nullable = true, length = 1)
    public String getIsCrossMkt() {
        return isCrossMkt;
    }

    public void setIsCrossMkt(String isCrossMkt) {
        this.isCrossMkt = isCrossMkt;
    }

    @Basic
    @Column(name = "Is_Mortgage", nullable = true, length = 1)
    public String getIsMortgage() {
        return isMortgage;
    }

    public void setIsMortgage(String isMortgage) {
        this.isMortgage = isMortgage;
    }

    @Basic
    @Column(name = "Listed_Date", nullable = true, precision = 0)
    public Integer getListedDate() {
        return listedDate;
    }

    public void setListedDate(Integer listedDate) {
        this.listedDate = listedDate;
    }

    @Basic
    @Column(name = "Delisted_Date", nullable = true, precision = 0)
    public Integer getDelistedDate() {
        return delistedDate;
    }

    public void setDelistedDate(Integer delistedDate) {
        this.delistedDate = delistedDate;
    }

    @Basic
    @Column(name = "Mkt_Type", nullable = true, length = 3)
    public String getMktType() {
        return mktType;
    }

    public void setMktType(String mktType) {
        this.mktType = mktType;
    }

    @Basic
    @Column(name = "PINYIN", nullable = true, length = 128)
    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Basic
    @Column(name = "PINYIN_FULL", nullable = true, length = 512)
    public String getPinyinFull() {
        return pinyinFull;
    }

    public void setPinyinFull(String pinyinFull) {
        this.pinyinFull = pinyinFull;
    }

    @Basic
    @Column(name = "Short_Name_EN", nullable = true, length = 64)
    public String getShortNameEn() {
        return shortNameEn;
    }

    public void setShortNameEn(String shortNameEn) {
        this.shortNameEn = shortNameEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PBondListInfo that = (PBondListInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(delflag, that.delflag) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(modifyDate, that.modifyDate) &&
                Objects.equals(username, that.username) &&
                Objects.equals(inputer, that.inputer) &&
                Objects.equals(checker, that.checker) &&
                Objects.equals(sts, that.sts) &&
                Objects.equals(bondKey, that.bondKey) &&
                Objects.equals(listedMarket, that.listedMarket) &&
                Objects.equals(bondId, that.bondId) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(isCrossMkt, that.isCrossMkt) &&
                Objects.equals(isMortgage, that.isMortgage) &&
                Objects.equals(listedDate, that.listedDate) &&
                Objects.equals(delistedDate, that.delistedDate) &&
                Objects.equals(mktType, that.mktType) &&
                Objects.equals(pinyin, that.pinyin) &&
                Objects.equals(pinyinFull, that.pinyinFull) &&
                Objects.equals(shortNameEn, that.shortNameEn);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, delflag, createDate, modifyDate, username, inputer, checker, sts, bondKey, listedMarket, bondId, shortName, isCrossMkt, isMortgage, listedDate, delistedDate, mktType, pinyin, pinyinFull, shortNameEn);
    }
}
