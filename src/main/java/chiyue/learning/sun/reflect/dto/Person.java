package chiyue.learning.sun.reflect.dto;

import com.alibaba.fastjson.JSONObject;

import chiyue.learning.sun.reflect.annotation.Self;

@SuppressWarnings("serial")
@Self
public class Person extends Obj implements java.io.Serializable {

	private int id;
	private String name;
	private double money;
	private String addr;
	
	public Person() {
		System.out.println("default Constructor");
	}
	
	public Person(int id, String name) {
		this.id = id;
		this.name = name;
		System.out.println("Constructor by tow params");
	}
	
	public static void publicSay(String...msgs) {
		for(String msg : msgs)
			System.out.println("public say: "+msg);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Self
	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
