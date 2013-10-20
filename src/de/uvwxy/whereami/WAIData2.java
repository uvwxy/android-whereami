package de.uvwxy.whereami;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import de.uvwxy.whereami.proto.Messages;

public class WAIData2 {
	private static final String LOCATION_LIST = "list.loc";
	private static final String HOME_LOCATION = "home.loc";
	ArrayList<Messages.Location> listLocations = new ArrayList<Messages.Location>();
	private Messages.Location homeLocation = null;

	public ArrayList<Messages.Location> getList() {
		return listLocations;
	}

	public int getLocationCount() {
		return listLocations.size();
	}

	public Messages.Location getHomeLocation() {
		return homeLocation;
	}

	public boolean delete(Context ctx, int position) {
		try {
			listLocations.remove(position);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		saveList(ctx);
		ActivityMain.listAdapter.notifyDataSetChanged();
		return true;
	}

	public synchronized boolean loadHomeLocation(Context ctx) {
		try {
			FileInputStream f = ctx.openFileInput(HOME_LOCATION);
			try {
				Messages.Location tempLoc = Messages.Location.parseDelimitedFrom(f);
				homeLocation = tempLoc;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean saveHomeLocation(Context ctx, Messages.Location loc) {
		FileOutputStream f;
		try {
			f = ctx.openFileOutput(HOME_LOCATION, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		try {
			loc.writeDelimitedTo(f);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		homeLocation = loc;
		return true;
	}

	public synchronized void loadList(Context ctx) {
		try {
			FileInputStream f = ctx.openFileInput(LOCATION_LIST);
			listLocations.clear();
			boolean readNext = true;
			while (readNext) {
				try {
					Messages.Location tempLoc = Messages.Location.parseDelimitedFrom(f);
					listLocations.add(tempLoc);

					if (f.available() == 0) {
						readNext = false;
					}
				} catch (IOException e) {
					e.printStackTrace();
					readNext = false;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean saveList(Context ctx) {
		FileOutputStream f;
		try {
			f = ctx.openFileOutput(LOCATION_LIST, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		for (Messages.Location loc : listLocations) {
			try {
				loc.writeDelimitedTo(f);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public synchronized boolean appendToList(Context ctx, Messages.Location loc) {
		if (ctx == null || loc == null) {
			return false;
		}
		FileOutputStream f;
		try {
			f = ctx.openFileOutput(LOCATION_LIST, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		try {
			loc.writeDelimitedTo(f);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


}
