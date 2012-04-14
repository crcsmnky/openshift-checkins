import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;

public class CheckinServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Mongo conn = null;
	private DB db = null;
	private DBCollection checkins = null;

	@Override
	public void init() throws ServletException {
		try {
			conn = new Mongo("127.0.0.1", 27017);
			db = conn.getDB("simplecheckin");
			//db.setReadPreference(ReadPreference.SECONDARY);

			//if (db.authenticate("admin", "aPYuQaBURFTu".toCharArray())) {
			//	throw new MongoException("unable to authenticate");
			//}

			checkins = db.getCollection("checkins");
			BasicDBObject geoIndex = new BasicDBObject("location", "2d");
			checkins.ensureIndex(geoIndex);
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (MongoException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		double x = 0.0;
		double y = 0.0;

		try {
			x = Double.valueOf(request.getParameter("x")).doubleValue();
			y = Double.valueOf(request.getParameter("y")).doubleValue();
		} catch (NullPointerException e) {
			response.sendError(response.SC_BAD_REQUEST,
					"missing arguments (double x, double y)");
		}
		BasicDBObject query = new BasicDBObject("location", new BasicDBObject(
				"$near", new double[] { x, y }));
		List<DBObject> results = checkins.find(query).toArray();

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		for (DBObject r : results) {
			out.println(r.toString());
		}
		out.close();
	}

	@SuppressWarnings("static-access")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		double x = 0.0;
		double y = 0.0;
		String comment = null;

		try {
			x = Double.valueOf(request.getParameter("x")).doubleValue();
			y = Double.valueOf(request.getParameter("y")).doubleValue();
			comment = request.getParameter("comment");
			if (comment == null) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			response.sendError(response.SC_BAD_REQUEST,
					"missing arguments (double x, double y, string comment)");
		}

		BasicDBObject doc = new BasicDBObject();
		doc.put("comment", comment);
		doc.put("location", new double[] { x, y });
		checkins.insert(doc);

		response.setStatus(response.SC_OK);
	}
}
