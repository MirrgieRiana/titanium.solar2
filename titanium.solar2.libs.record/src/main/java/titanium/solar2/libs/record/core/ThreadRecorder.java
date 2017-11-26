package titanium.solar2.libs.record.core;

import java.time.LocalDateTime;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.util.AttributesBuilder;

public class ThreadRecorder extends Thread
{

	private Recorder recorder;

	private long lastTime;

	public ThreadRecorder(Recorder recorder)
	{
		this.recorder = recorder;
	}

	@Override
	public void run()
	{
		lastTime = System.nanoTime();
		while (true) {
			LocalDateTime time = LocalDateTime.now();

			// 記録前の段階で取得可能だったデータ数を取得
			int available = recorder.targetDataLine.available();

			// 記録と計測時間の計測
			long t1 = System.nanoTime();
			Buffer buffer = recorder.buffers.getBuffer();
			int length = recorder.targetDataLine.read(buffer.array, 0, buffer.array.length);
			long t2 = System.nanoTime();
			long readTime = t2 - t1;

			// ループ時間の計算
			long now = System.nanoTime();
			long loopTime = now - lastTime;
			lastTime = now;

			recorder.chunkStorage.push(new Chunk(buffer, length, new AttributesBuilder()
				.add("Available", "" + available)
				.add("ReadSeconds", "%.2f", readTime * 1e-9)
				.add("LoopSeconds", "%.2f", loopTime * 1e-9)
				.get(), time));
		}
	}

}
