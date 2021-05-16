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

## ToDo List
- [ ] Copy and rewrite all necessary code from the old app to enable all public accessible sites without an edit options.
- [ ] read, copy, do and maybe extend old ToDo list from the [old account website](https://tobi.frdy.eu/neu/todo.php) by B3tterTogeth3r.

## Fragments
The names of the public fragments:
- [ ] about_us
- [x] chargen
  - On first selection it just displays the current student board. By click on the top toolbar the user can change the displayed semester.
- [x] chargen_phil
  - On first selection it just displays the current "philister"-board. By click on the top toolbar the user can change the displayed semester.
- [ ] donate
  - The IBANs to donate money to the fraternity are shown. If the user clicks on one IBAN, it will be copied to the clipboard. 
- [ ] frat_ger
  - Displays a short history of fraternities in Germany. Below is a list with the biggest fraternity umbrella organisation. 
- [ ] frat_wue
  - ehm.. 
- [ ] home
  - displays the greeting of the current semester. below that is a slide show with images. Below the slide show is a list of all the notes of the _Semesterprogramm_.
- [ ] news
  - A list of news is shown. If a new one is written into Firestore, the user will get a push message or, if the app is open, the list will automatically update itself.
- [ ] own_history
- [ ] program
  - A list with all the events in the selected semester is shown. Every element is clickable. On click a dialog will open and show a detailed version of the event, including but not limited with the approximated end time and the locations address.
- [ ] rooms
- [ ] settings

The names of the private fragments
- [ ] balance
- [ ] drink
- [ ] new_person
- [ ] new_semester
- [ ] transcript
- [ ] user_control
- [ ] profile

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
 
