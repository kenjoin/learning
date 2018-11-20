package chiyue.learning.ping;

import java.net.InetAddress;

public class Ping {

	public static void ping(String domain) {
		InetAddress address;
		try {
			address = InetAddress.getByName(domain);
			System.out.println("Name:" + address.getHostName());
			System.out.println("Addr:" + address.getHostAddress());
			System.out.println("Reach:" + address.isReachable(3000)); // 是否能通信 返回true或false
			System.out.println(address.toString());
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		ping("www.baidu.com");
		ping("www.qq.com");
		ping("www.weibo.com");
		ping("newbp.yixinfa.hk");
		ping("newbp.yixinfa.cn");
	}
	
}
