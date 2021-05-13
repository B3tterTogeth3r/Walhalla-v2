# Walhalla-v2
New App<br>
Had to start a new App because after updates of Windows and AndroidStudio the saved password to create signed apks was wrong and the SHA fingerprints changed resulting in a new encryption key.
<p>
Whats new:<br>
Filled the home.fragment with some custom functions. Cleaned up the code by extending the abstraction of CustomFragment.java with more abstract functions. Started to comment every function and public accessible variables for better code understanding. Changed the display of the image_slider.xml<br>
ImageDownload does not block the UI thread anymore.<br>
Re-added the program.fragment, removed the add/edit site, because it is ugly to read in code and the ui.<br>
Redesigned code and ui of semester picker for better readability.<br>
created a dialog.xml to use for every dialog. In it inflate e.g. dialog_item_sem_change.xml<br>
<p>
Chargen<br>
Changed a bit of the old chargen.fragment code, because of the new design of CustomFragment.class<br>
Added ability of ChangeSemesterDialog to start with the chosen semester. Also it changes the year depending on the users choice for WS or SS.<br>
<p>
Chargen_phil<br>
Changed_phil a bit of the old chargen_phil.fragment code, because of the new design of CustomFragment.class
<p>
