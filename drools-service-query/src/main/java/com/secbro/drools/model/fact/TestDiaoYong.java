package com.secbro.drools.model.fact;

public class TestDiaoYong {
	private Boolean num;

	public static void run(String postcode) {
		System.out.println("规则执行完成之后调用的方法=="+postcode);
	}

	public Boolean getNum() {
		return num;
	}

	public void setNum(Boolean num) {
		this.num = num;
	}
}
