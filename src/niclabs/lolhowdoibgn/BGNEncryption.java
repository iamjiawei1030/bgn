package niclabs.lolhowdoibgn;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.math.BigIntegerUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;



public class BGNEncryption extends IntentService {
	public BGNEncryption() {
		super("BGNEncryption");
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = "BGNEncryption";
	public static final String start = "start";
	public static final String end = "end";
	private PairingParameters param;
	private BigInteger r;
	private BigInteger q; //This is the private key.
	private BigInteger order;
	private SecureRandom rng;
	private String message = "000";

	@SuppressLint("TrulyRandom")
	public PublicKey gen(int bits) {
		rng = new SecureRandom();
		TypeA1CurveGenerator a1 = new TypeA1CurveGenerator(rng, 2, bits); //Requires 2 prime numbers.
		param = a1.generate();
		TypeA1Pairing pairing = new TypeA1Pairing(param);
		order = param.getBigInteger("n"); //Must extract the prime numbers for both keys.
		r = param.getBigInteger("n0");
		q = param.getBigInteger("n1");
		Field<?> f = pairing.getG1();
		Element P = f.newRandomElement();
		P = P.mul(param.getBigInteger("l"));
		Element Q = f.newElement();
		Q = Q.set(P);
		Q = Q.mul(r);
		return new PublicKey(pairing, P, Q,
				order);
	}

	public Element encrypt(PublicKey PK, String msg) {
		BigInteger t = BigIntegerUtils.getRandom(PK.getN());
		int m = msg.hashCode(); //Should be replaced with own hash.
		System.out.println("Hash is " + m);
		Field<?> f = PK.getField();
		Element A = f.newElement();
		Element B = f.newElement();
		Element C = f.newElement();
		A = A.set(PK.getP());
		A = A.mul(BigInteger.valueOf(m));
		B = B.set(PK.getQ());
		B = B.mul(t);
		C = C.set(A);
		C = C.add(B);
		return C;
	}
	
	public Element add(PublicKey PK, Element A, Element B){
		BigInteger t = BigIntegerUtils.getRandom(PK.getN());
		Field<?> f = PK.getField();
		Element output = f.newElement();
		Element aux = f.newElement();
		aux.set(PK.getQ());
		aux.mul(t);
		output.set(A);
		output.add(B);
		output.add(aux);
		return output;
	}
	
	public Element mul(PublicKey PK, Element C, Element D){
		BigInteger t = BigIntegerUtils.getRandom(PK.getN()); //Make sure product is NOT infinite.
		Field<?> f = PK.getField();
		Element Q = PK.getQ();
		Element output = f.newElement();
		Element aux = f.newElement();
		aux.set(PK.doPairing(C, D));
		output.set(PK.doPairing(Q,Q));
		output.pow(t);
		output.mul(aux);
		return output;
	}

	public String decrypt(PublicKey PK, BigInteger sk, Element C) {
		Field<?> f = PK.getField();
		Element T = f.newElement();
		Element K = f.newElement();
		Element aux = f.newElement();
		T = T.set(PK.getP());
		T = T.mul(sk);
		K = K.set(C);
		K = K.mul(sk);
		aux = aux.set(T);
		BigInteger m = new BigInteger("1");
		while (!aux.isEqual(K)) {
			//This is a brute force implementation of finding the discrete logarithm.
			//Performance may be improved using algorithms such as Pollard's Kangaroo.
			aux = aux.add(T);
			m = m.add(BigInteger.valueOf(1));
		}
		return m.toString();
	}

	public void setMessage(String m) {
		this.message = m;
	}

	public void stop() {
		Log.i(TAG, "Stop.%n");
	}

	public static void main(String[] args) {
		BGNEncryption b = new BGNEncryption();
		PublicKey PK = b.gen(32);
		Element msg = b.encrypt(PK, b.message);
		Element msg2 = b.encrypt(PK, "111");
		long t = System.currentTimeMillis();
		System.out.println(b.decrypt(PK, b.q, b.add(PK, msg, msg2)));
		System.out.println("Decryption took "
				+ (System.currentTimeMillis() - t) + " ms");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Intent bgn = new Intent();
		bgn.setAction(start);
		sendBroadcast(bgn);
		Bundle b = intent.getExtras();
		String message = b.getString("message");
		int nbits = b.getInt("bits");
		Log.d("STEP", "Generating Key");
		PublicKey PK = this.gen(nbits);
		Log.d("STEP", "Encrypting");
		bgn.putExtra("Progress", 40);
		sendBroadcast(bgn);
		Element msg = this.encrypt(PK, message);
		Log.d("STEP", "Done!");
		bgn.putExtra("Progress", 80);
		sendBroadcast(bgn);
		b.putString("message", msg.toString());
		b.putString("secret", this.q.toString());
		bgn.putExtras(b);
		bgn.setAction(end);
		Log.d("STEP", "Broadcasting");
		sendBroadcast(bgn);
	}
}
