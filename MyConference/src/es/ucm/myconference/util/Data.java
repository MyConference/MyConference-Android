package es.ucm.myconference.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.ucm.myconference.R;
import android.database.Cursor;
import android.database.MatrixCursor;

public final class Data {

	private static long index = 0;
	private static MatrixCursor slideMenu;
	private static MatrixCursor programCursor;
	
	public static Cursor getSlideMenuCursor(String[] items){
		if(slideMenu == null){
			slideMenu = new MatrixCursor(new String[] { "_id", "item", "icon" } );
			
			slideMenuRow(items[0], R.drawable.ic_menu_new);
			slideMenuRow(items[1], R.drawable.ic_menu_papers);
			slideMenuRow(items[2], R.drawable.ic_menu_committee);
			slideMenuRow(items[3], R.drawable.ic_menu_speakers);
			slideMenuRow(items[4], R.drawable.ic_menu_venue);
			slideMenuRow(items[5], R.drawable.ic_menu_travel);
			slideMenuRow(items[6], R.drawable.ic_menu_program);
			slideMenuRow(items[7], R.drawable.ic_menu_links);
			slideMenuRow(items[8], R.drawable.ic_menu_about);
		}
		
		return slideMenu;
	}
	
	private static void slideMenuRow(String name, int icon){
		slideMenu.addRow(new Object[] {++index, name, icon} );
	}
	
	public static Cursor getProgramCursor(){
		if(programCursor == null){
			programCursor = new MatrixCursor(new String[] {"_id", "title", "description", "day", "start_time"});
			
			final String MAY_16 = "May, 16";
			final String MAY_17 = "May, 17";
			final String MAY_18 = "May, 18";
			
			programRow(
					"Tutorial session", "At Shanghai University of Finance and Economics (see Conference Venue for address)",
					MAY_16, "15:00~17:00");
			programRow(
					"Welcome and Opening Ceremony, the main conference and keynote speeches",
					"At Golden Jade Sunshine Hotel (see Conference Venue for address)", MAY_17, "Morning");
			programRow("Parallel sessions", "At Golden Jade Sunshine Hotel", MAY_17, "Afternoon");
			programRow("Welcoming banquet", null, MAY_17, "Evening");
			programRow("Parallel sessions", "At Golden Jade Sunshine Hotel", MAY_18, "Morning");
			programRow("Sightseeing in Shanghai", null, MAY_18, "Afternoon");
			programRow("Dinner and closing of PIC-2014", null, MAY_18, "Evening");
			
		}
		
		return programCursor;
	}
	
	private static void programRow(String title, String descrp, String day, String time){
		programCursor.addRow(new Object[] {++index, title, descrp, day, time});
	}
	
	public static String inputStreamToString(InputStream inputStream){
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        StringBuilder result = new StringBuilder();
        
        try {
			while((line = bufferedReader.readLine()) != null){
			    result.append(line);
			}
	        inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
        return result.toString();
	}
}
