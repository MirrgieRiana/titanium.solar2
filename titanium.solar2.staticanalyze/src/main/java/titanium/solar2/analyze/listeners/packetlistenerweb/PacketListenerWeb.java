package titanium.solar2.analyze.listeners.packetlistenerweb;

import java.net.InetSocketAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Hashtable;
import java.util.Optional;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.cgi.HTTPResponse;
import net.arnx.jsonic.JSON;
import titanium.solar2.analyze.Packet;
import titanium.solar2.libs.analyze.IItemListener;
import titanium.solar2.libs.time.ITimeRenderer;

public class PacketListenerWeb implements IItemListener<Packet>
{

	private double samplesPerSecond;
	private String host;
	private int port;
	private int backlog;
	private int webSocketPort;
	private ITimeRenderer timeRenderer;

	private HttpServer httpServer;
	private WebSocketServer webSocketServer;

	public PacketListenerWeb(
		double samplesPerSecond,
		String host,
		int port,
		int backlog,
		int webSocketPort,
		ITimeRenderer timeRenderer)
	{
		this.samplesPerSecond = samplesPerSecond;
		this.host = host;
		this.port = port;
		this.backlog = backlog;
		this.webSocketPort = webSocketPort;
		this.timeRenderer = timeRenderer;
	}

	@Override
	public void preAnalyze()
	{
		try {
			httpServer = HttpServer.create();
			httpServer.bind(new InetSocketAddress(host, port), backlog);

			httpServer.createContext("/api/streamPort", e -> {
				HTTPResponse.send(e, 200, "" + webSocketPort);
			});
			httpServer.createContext("/", e -> {
				String path = e.getRequestURI().getPath();
				if (path.endsWith("/")) {
					HTTPResponse.redirect(e, path + "index.html");
				} else {
					URL url = PacketListenerWeb.class.getResource(path.substring(1));
					if (url != null) {
						HTTPResponse.sendFile(e, url);
						return;
					}
				}
				HTTPResponse.send(e, 404, "404");
			});

			webSocketServer = new WebSocketServer(new InetSocketAddress(host, webSocketPort)) {
				@Override
				public void onStart()
				{

				}

				@Override
				public void onOpen(WebSocket conn, ClientHandshake handshake)
				{

				}

				@Override
				public void onMessage(WebSocket conn, String message)
				{
					System.out.println("onMessage: " + conn + " " + message);
				}

				@Override
				public void onClose(WebSocket conn, int code, String reason, boolean remote)
				{

				}

				@Override
				public void onError(WebSocket conn, Exception ex)
				{
					ex.printStackTrace();
				}
			};

			httpServer.start();
			webSocketServer.start();

			System.err.println("HTTP Server Start: " + host + ":" + port);
			System.err.println("WebSocket Server Start: " + host + ":" + webSocketPort);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int id = 0;
	private Long firstTime = null;

	@Override
	public void onItem(Packet packet)
	{
		onChain(packet.binary, packet.getFirstPulse().getTime(samplesPerSecond), packet.getBytes());
	}

	private void onChain(String binary, LocalDateTime time, Optional<int[]> bytes)
	{
		if (firstTime == null) firstTime = time.toInstant(ZoneOffset.UTC).toEpochMilli();
		int timeInt = (int) (time.toInstant(ZoneOffset.UTC).toEpochMilli() - firstTime);

		for (WebSocket connection : webSocketServer.connections()) {
			try {
				connection.send(toString(id, binary, time, timeInt, bytes));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		id++;
	}

	private String toString(int id, String binary, LocalDateTime time, int timeInt, Optional<int[]> bytes)
	{
		return JSON.encode(new Hashtable<String, Object>() {
			{
				put("id", id);
				put("binary", binary);
				put("time", timeRenderer.format(time));
				put("time_int", timeInt);
				put("bytes", bytes);
			}
		});
	}

	@Override
	public void postAnalyze()
	{
		try {
			httpServer.stop(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			webSocketServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
