package ir.royapajoohesh.itunesclient.net;

public interface OperationListener {
	void onSucceed();

	void onError(String data);
}