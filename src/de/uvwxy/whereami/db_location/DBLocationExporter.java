package de.uvwxy.whereami.db_location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.util.Log;
import de.uvwxy.whereami.proto.Messages;

public class DBLocationExporter {
	private String filePath;
	private ArrayList<Messages.Location> dbArray;

	public DBLocationExporter(String filePath, ArrayList<Messages.Location> dbArray) {
		super();
		this.filePath = filePath;
		this.dbArray = dbArray;
	}

	public int export(String charset) {
		if (filePath == null)
			return -1;
		if (dbArray == null)
			return -2;

		File f = new File(filePath);

		if (f.exists())
			return -3;

		FileOutputStream fout;
		try {
			fout = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			Log.d("DBEXPORTER", "Failed creating FileOutPutStream");
			return -4;
		}

		int numLinesWritten = 0;

		try {
			OutputStreamWriter out = new OutputStreamWriter(fout, Charset.forName(charset));
			for (int i = 0; i < dbArray.size(); i++) {
				try {
					out.write(mkLine(dbArray.get(i)));
					if (i != dbArray.size() - 1) {
						out.write("\n");
					}
					numLinesWritten++;
				} catch (IOException e) {
					Log.d("DBEXPORTER", "Failed during writing to file...");
					out.close();
					fout.close();
					return -5;
				}
			}

			out.close();
			fout.close();
		} catch (IOException e) {
			Log.d("DBEXPORTER", "Failed closing FileOutputStreamf..");
			return -6;
		}

		return numLinesWritten;
	}

	private String mkLine(Messages.Location entry) {
		String chard = ",";
		String ret = "\"" + entry.getName() + "\"" + chard //
				+ entry.getLatitude() + chard //
				+ entry.getLongitude() + chard //
				+ entry.getAltitude() + chard //
				+ entry.getBearing() + chard //
				+ entry.getSpeed() + chard //
				+ entry.getTime() + chard //
				+ entry.getProvider();
		return ret;
	}
}