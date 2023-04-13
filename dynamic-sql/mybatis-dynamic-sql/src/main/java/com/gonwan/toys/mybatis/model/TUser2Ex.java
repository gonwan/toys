package com.gonwan.toys.mybatis.model;

import com.gonwan.toys.mybatis.generated.model.TUser2;
import com.gonwan.toys.mybatis.generated.model.TUser3;

public class TUser2Ex extends TUser2 {

    private TUser3 tUser3;

    public TUser3 gettUser3() {
        return tUser3;
    }

    public void settUser3(TUser3 tUser3) {
        this.tUser3 = tUser3;
    }

}
