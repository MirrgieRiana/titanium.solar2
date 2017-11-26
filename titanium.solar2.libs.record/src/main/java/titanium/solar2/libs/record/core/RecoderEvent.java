package titanium.solar2.libs.record.core;

import titanium.solar2.libs.record.util.AttributesBuilder;

public class RecoderEvent
{

	public static class Ready extends RecoderEvent
	{

	}

	public static class Start extends RecoderEvent
	{

	}

	public static class Destroy extends RecoderEvent
	{

	}

	public static class ProcessChunk extends RecoderEvent
	{

		public final Chunk chunk;

		public ProcessChunk(Chunk chunk)
		{
			this.chunk = chunk;
		}

		public static class Pre extends ProcessChunk
		{

			public Pre(Chunk chunk)
			{
				super(chunk);
			}

		}

		public static class Consume extends ProcessChunk
		{

			public final AttributesBuilder attributesBuilder;

			public Consume(Chunk chunk, AttributesBuilder attributesBuilder)
			{
				super(chunk);
				this.attributesBuilder = attributesBuilder;
			}

		}

		public static class Post extends ProcessChunk
		{

			public Post(Chunk chunk)
			{
				super(chunk);
			}

		}

	}

}
