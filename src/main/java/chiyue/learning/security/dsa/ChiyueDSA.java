package chiyue.learning.security.dsa;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import chiyue.learning.security.rsa.tools.BytesHexStrTranslate;

public class ChiyueDSA {
	
	public final static String src = "zzzzzz";
	
	public static void main(String[] args) {
		jdkDSA();
	}
	
	
	public static void jdkDSA() {
		try {
			//1.初始化密钥
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
			keyPairGenerator.initialize(512);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			DSAPublicKey dsaPublicKey = (DSAPublicKey) keyPair.getPublic();
			DSAPrivateKey dsaPrivateKey = (DSAPrivateKey) keyPair.getPrivate();
			
			String str1 = BytesHexStrTranslate.bytesToHexFun1(dsaPublicKey.getEncoded());
			String str2 = BytesHexStrTranslate.bytesToHexFun1(dsaPrivateKey.getEncoded());
			
			System.out.println(str1);
			System.out.println(str2);
			
			//2.执行签名
			PKCS8EncodedKeySpec pkcs8EncodeKeySpec = new PKCS8EncodedKeySpec(dsaPrivateKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance("DSA");
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodeKeySpec);
			Signature signature = Signature.getInstance("SHA1withDSA");
			signature.initSign(privateKey);
			signature.update(src.getBytes());
			byte[] result = signature.sign();
			String hexString = BytesHexStrTranslate.bytesToHexFun1(result);
			System.out.println("result:" + hexString);
			
			//3.验证签名
			X509EncodedKeySpec x509EncodeKeySpec = new X509EncodedKeySpec(dsaPublicKey.getEncoded());
			keyFactory = KeyFactory.getInstance("DSA");
			PublicKey publicKey = keyFactory.generatePublic(x509EncodeKeySpec);
			signature = Signature.getInstance("SHA1withDSA");
			signature.initVerify(publicKey);
			signature.update(src.getBytes());
			boolean b = signature.verify(result);
			System.out.println("verify result:"+ b);
			

			KeyPairGenerator keyPairGenerator1 = KeyPairGenerator.getInstance("DSA");
			keyPairGenerator1.initialize(512);
			KeyPair keyPair1 = keyPairGenerator1.generateKeyPair();
			DSAPublicKey dsaPublicKey1 = (DSAPublicKey) keyPair1.getPublic();
			
			X509EncodedKeySpec x509EncodeKeySpec1 = new X509EncodedKeySpec(dsaPublicKey1.getEncoded());
			keyFactory = KeyFactory.getInstance("DSA");
			PublicKey publicKey1 = keyFactory.generatePublic(x509EncodeKeySpec1);
			
			signature = Signature.getInstance("SHA1withDSA");
			signature.initVerify(publicKey1);
			signature.update(src.getBytes());
			
			System.out.println(BytesHexStrTranslate.bytesToHexFun1(publicKey1.getEncoded()));
			System.out.println("ok?" + signature.verify(result));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
