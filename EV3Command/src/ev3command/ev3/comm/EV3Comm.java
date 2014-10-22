package ev3command.ev3.comm;

public interface EV3Comm {

	void open() throws Exception;

	public void sendData(byte[] request);

	public byte[] readData();

	public void close();

}