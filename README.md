# Walhalla-v2
## New App
*I had to start a new App because after updates of Windows and AndroidStudio the saved password to create signed apks was wrong and the SHA fingerprints changed resulting in a new encryption key.*<br>
I try to add every commit and push message in here, formatted like<br>
\## Title<br>
\*\*Time\*\*<br>
\- Context

## Table of Contents
Select a  commit:
- [ToDo List](#todo-list)
- [Whats new](#whats-new)
- [Chargen](#chargen)
- [Chargen_phil](#chargen_phil)
- [News](#news)
- [New Fragments and Functions](#new-fragments-and-functions) (latest)

## ToDo List
- [ ] Copy and rewrite all necessary code from the old app to enable all public accessible sites without an edit options.
- [ ] read, copy, do and maybe extend old ToDo list from the [old account website](https://tobi.frdy.eu/neu/todo.php) by B3tterTogeth3r.
- [ ] Find a way to save a checkbox in Firestore and recognise it in the app.

- [ ] Program: Set Google Maps Android API up correctly.
- [ ] Image slide show:
  - automatic switch between images
  - swipe gestures are possible

- [ ] History: 
  - The "summary" version has to be even shorter or formatted different. A Table maybe?
  - Display not everything on one side. Maybe buttons at the bottom/top to display more?

## Fragments
The names of the public fragments without their edit buttons and functions:
- [x] about_us
  - A description of the fraternity, the place, the ways to the uni.
- [x] chargen
  - On first selection it just displays the current student board. By click on the top toolbar the user can change the displayed semester.
- [x] chargen_phil
  - On first selection it just displays the current "philister"-board. By click on the top toolbar the user can change the displayed semester.
- [x] donate
  - The IBANs to donate money to the fraternity are shown. If the user clicks on one IBAN, it will be copied to the clipboard. 
- [x] frat_ger
  - Displays a short history of fraternities in Germany. Below is a list with the biggest fraternity umbrella organisation. 
- [x] frat_wue
  - Show a map and a table with all fraternities in Wuerzburg
- [x] home
  - displays the greeting of the current semester. below that is a slide show with images. Below the slide show is a list of all the notes of the _Semesterprogramm_.
- [x] news
  - A list of news is shown. If a new one is written into Firestore, the user will get a push message or, if the app is open, the list will automatically update itself.
- [x] own_history
- [ ] program
  - A list with all the events in the selected semester is shown. Every element is clickable. 
  - On click a dialog will open and show a detailed version of the event, including but not limited with the approximated end time and the locations address.
- [x] rooms

The names of the private fragments
- [ ] balance
- [ ] drink
- [ ] new_person
- [ ] new_semester
- [ ] transcript
- [ ] user_control
- [ ] profile
- [ ] settings

Fragments that can edit their displayed content:
- [ ] about_us
- [ ] chargen
- [ ] chargen_phil
- [ ] donate 
- [ ] frat_ger 
- [ ] frat_wue
- [ ] home
- [ ] news
- [ ] own_history
- [ ] program
- [ ] rooms

## Whats new:
- Filled the home.fragment with some custom functions. Cleaned up the code by extending the abstraction of CustomFragment.java with more abstract functions. Started to comment every function and public accessible variables for better code understanding. Changed the display of the image_slider.xml
- ImageDownload does not block the UI thread anymore.
- Re-added the program.fragment, removed the add/edit site, because it is ugly to read in code and the ui.
- Redesigned code and ui of semester picker for better readability.
- Created dialog.xml to use for every dialog. In it inflate e.g. dialog_item_sem_change.xml

## Chargen
- Changed a bit of the old chargen.fragment code, because of the new design of CustomFragment.class
- Added ability of ChangeSemesterDialog to start with the chosen semester. Also it changes the year depending on the users choice for WS or SS.

## Chargen_phil
- Changed_phil a bit of the old chargen_phil.fragment code, because of the new design of CustomFragment.class

## News
13.05.2021 13:51
- Copied news.fragment from the old app. The dialog including the menu and onclick functions are still disabled, because of the yet missing auth functions I still have to redo in order to create, edit and delete users from inside the app.
 
## New Fragments and Functions
17.05.2021
1. About Us
   - Added content to about_us.Fragment. compared to the old one the data is now completely formatted and then displayed via Sites.java
2. utils.Sites.create()
   - Creates a Layout with the given data. Using this class all displayed data will always look the same on the ui.
3. utils.Slider.load()
   - Fills the given relative layout with the given amount of images. Here are 2 new ToDo items.
4. Updated annotations in CustomFragment.java
5. Also removed watermark from images in News.
6. Fraternities in Germany
   - Filled the content in it.
7. Fraternities in Würzburg
   - Filled the content in it.
8. Own history
   - Filled the content in it and added some relating TODOs to the top.
9. Rooms
   - Filled the content in it and added some relating TODOs to the top
10. Donate
    - Filled the content, fitted the new design, removed some unnecessary code.