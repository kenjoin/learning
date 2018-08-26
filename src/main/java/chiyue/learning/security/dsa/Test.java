package chiyue.learning.security.dsa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Test {

	public static byte[] decyptBase64(String key) {

		InputStream is = new ByteArrayInputStream(key.getBytes());

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];

		int len = -1;

		byte[] data = bos.toByteArray();

		try {
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}

			data = bos.toByteArray();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = Base64.getDecoder().decode(data);
		return data;
	}

	public static String pub = "3081f03081a806072a8648ce38040130819c024100fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151bdc43ee737592e17021500962eddcc369cba8ebb260ee6b6a126d9346e38c50240678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a6c416e50be794ca403430002405a56f57aed5b48bb5bbf30f922f28410793386e8056f58ed8439b39312adbe857a2ea5987a351ae475b987f31300bc67e8e744a4d6918a40a63f2bba6b677888";
	public static String pri = "3081c60201003081a806072a8648ce38040130819c024100fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151bdc43ee737592e17021500962eddcc369cba8ebb260ee6b6a126d9346e38c50240678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a6c416e50be794ca4041602141967747bf84c29a079d4d3aad258b42e79ecb4f1";
	public static String sign = "302c02147d0c02513f3e964816538069f763a46ef1f411a90214413a26cd804db3c94004df944d8e71300034c0b7";
	
	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		
		KeySpec keySpec = new X509EncodedKeySpec((decyptBase64(pub)));
		
		PublicKey publicKey = KeyFactory.getInstance("DSA").generatePublic(keySpec);
		
		Signature signature = Signature.getInstance("SHA1withDSA");
		
		signature.initVerify(publicKey);
		
		byte[] encodeKey = ChiyueDSA.src.getBytes();
		
		signature.update(encodeKey);
		
		System.out.println(signature.verify(sign.getBytes()));
		
	}

}
