package ir.royapajoohesh.utils.net;

public interface DownloadEventsListener<SucceedDataType, ErrorDataType> {

	public void onSucceed(SucceedDataType data, boolean hasError);

	public void onError(ErrorDataType data);

	public void onInitProgress(String title, int maxValue);

	public void onSetProgressValue(String title, int val);

	public void onIncreaseProgressValue(String title, int val);

	public void onAbort();
}