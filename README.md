# Walhalla-v2
<p>I started the app as a simple web tool to manage the account easier. Soon I realized that having
 the account was not enough for events like a trip to another fraternity or something like that. 
 Because I already needed the semesters program inside, I choose to make that part public accessible.
 For drives or a drink calculation and its billing I also needed to have all the members of the 
  fraternity at least by name. Also I did not want to do all of that by myself so I had to give
  certain permissions to add/edit/delete events, bills and drinks to certain people, the "chargen" 
  of the current semester. Programming all of that I realized, I do not want to delete public data
  like programs or old chargen, so I had to make them accessible, too, but only to read, not to 
  edit. While programming I came across the possibility to put here a complete member list, because
  nearly everybody had to have at least once a "charge". So I had to recreate the registration 
  process and added a personal balance field for easier drink-billing.<br>
  After having all these ideas I realized I didn't had the performance and data plan I needed to
  update all the data I wanted to display to the user. So I changed my complete data structure from
  one SQL database online and a complete copy to a locale SQLi-database on Android to the NoSQL 
  version of Googles Firebase Cloud Firestore. By doing so I had to restructure the way I saved 
  "chargen", events, details to that events and the account of that semester. But also I had to 
  relearn how to structure effectively my code so I do not come to a point where the user has to 
  have an internet connection to use the app at all, so I had to learn to use Google Remote Config.
  That cost me another two weeks. While learning to structure my data, I learned a bit more about
  JavaScript and TypeScript so I could use the the same data structure for a browser site display.
  I used TS to send push messages to all the users, if a new news-message was written into 
  Firestore. <br>
  After showing the `Alpha 2.x` to Felix and Ansgar, both said they wanted to write some code for
  it. Felix wants to write a chat, Ansgar still has to learn a lot about coding in Java, so maybe
  someday in the near future he can choose a inner project for himself.</p>
<p>
I copied all the public and some private texts and images from the current website 
(http://walhalla-wuerzburg.de/) to add some more text and public sites to the app, but they have to 
be rewritten. Some have to get shortened or just completely redesigned for a mobile app. Nearly 
nobody wants to read a 5 A4 page long text inside an app from the beginning.</p>

## Rules to this file:
Add every push and commit message in here, because sometimes parts of messages disappear. 
Format them like that:
\## Title\<br><br>
\*\*Time\*\*\<br><br>
\*Version code:\* \``ReleaseNumber` _ `ReleaseVersion` _ `ddMMyy`\` \*- Version name:\* \`name\`\<br><br> 
\- Context

## Table of Contents
Select a  commit:
- [ToDo List](#todo-list)
- [Fragments](#fragments)
- [App v2](#new-app)
- [Whats new](#whats-new)
- [Chargen](#chargen)
- [Chargen_phil](#chargen_phil)
- [News](#news)
- [New Fragments and Functions](#new-fragments-and-functions)
- [A lot again](#a-lot-again) 
- [A little bit new and many, many comments](#a-little-bit-new-and-many-many-comments) 
- [Registration, profile, ana- and crashlytics, design and comments](#registration-profile-ana--and-crashlytics-design-and-comments)
- [Some new Stuff and more ToDos](#some-new-stuff-and-more-todos)
- [Fixed bugs and some more functional fragments](#fixed-bugs-and-some-more-functional-fragments) (latest)

## ToDo List
- [ ] Comment every method, function, constructor and class itself for better understandability
 in the future.
- [ ] A signed in user has to be in the audience according to his rank.
- [ ] Find a way to save more data into Firebase Auth and Firestore and write a Cloud Function 
to connect them all.
- [ ] Copy and rewrite all necessary code from the old app to enable all public accessible sites 
without an edit options.
- [ ] read, copy, do and maybe extend old ToDo list from the [old accounting website](https://tobi.frdy.eu/neu/todo.php) by B3tterTogeth3r.
- [ ] Find a way to save a checkbox in Firestore and recognise it in the app.

- [ ] Program: 
  - [ ] Set Google Maps Android API up correctly.
  - [ ] edit/add/remove public and internal events
  - [ ] add/edit/delete *helpers* with tasks to an event
    - [ ] with possibility of the user to mark that task as done

- [ ] Image slide show:
  - [ ] automatic switch between images
  - [ ] swipe gestures are possible

- [ ] History: 
  - [ ] The "summary" version has to be even shorter or formatted different. A Table maybe?
  - [ ] Display not everything on one side. Maybe buttons at the bottom/top to display more?
- [ ] Drink:
  - [ ] Add a invoice by push to everybody with balance < 0
  - [ ] add/edit/delete drinks
  - [ ] display of whole semester account (for drinks)
  - [ ] remind members with a balance < 0 via push message
  - [ ] display a table with members sorted by amount consumed
- [ ] Transcript:
  - add/edit/delete new transcripts to events
  - read existing transcripts only while chosenSemester > joinedSemester  

## Fragments
The names of the public fragments without their edit buttons and functions:
- [x] about_us
  - A description of the fraternity, the place, the ways to the uni.
- [x] chargen
  - On first selection it just displays the current student board. By click on the top toolbar
   the user can change the displayed semester.
- [x] chargen_phil
  - On first selection it just displays the current "philister"-board. By click on the top toolbar
   the user can change the displayed semester.
- [x] donate
  - The IBANs to donate money to the fraternity are shown. If the user clicks on one IBAN, 
  it will be copied to the clipboard. 
- [x] frat_ger
  - Displays a short history of fraternities in Germany. Below is a list with the biggest 
  fraternity umbrella organisation. 
- [x] frat_wue
  - Show a map and a table with all fraternities in Wuerzburg
- [x] home
  - displays the greeting of the current semester. below that is a slide show with images. Below 
  the slide show is a list of all the notes of the _Semesterprogramm_.
- [x] news
  - A list of news is shown. If a new one is written into Firestore, the user will get a push 
  message or, if the app is open, the list will automatically update itself.
- [x] own_history
- [ ] program
  - [x] A list with all the events in the selected semester is shown. Every element is clickable. 
  - [ ] On click a dialog will open and show a detailed version of the event, including but not 
  limited to the approximated end time and the locations address.
- [x] rooms

The names of the private fragments
- [x] balance
- [ ] drink
- [ ] new_person
- [ ] new_semester
- [ ] transcript
- [ ] user_control
- [x] profile
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

## New App
I had to start a new App, because, after updates of Windows and AndroidStudio, the saved password 
to create signed apks was wrong and resulting in a new encryption key the SHA fingerprints changed.

## Whats new:
- Filled the home.fragment with some custom functions. Cleaned up the code by extending the 
abstraction of CustomFragment.java with more abstract functions. Started to comment every function 
and public accessible variables for better code understanding. Changed the display of the image_slider.xml
- ImageDownload does not block the UI thread anymore.
- Re-added the program.fragment, removed the add/edit site, because it is ugly to read in code and 
the ui.
- Redesigned code and ui of semester picker for better readability.
- Created dialog.xml to use for every dialog. In it inflate e.g. dialog_item_sem_change.xml

## Chargen
- Changed a bit of the old chargen.fragment code, because of the new design of CustomFragment.class
- Added ability of ChangeSemesterDialog to start with the chosen semester. Also it changes the year
 depending on the users choice for WS or SS.

## Chargen_phil
Changed_phil a bit of the old chargen_phil.fragment code, because of the new design of CustomFragment.class

## News
13.05.2021 13:51
Copied news.fragment from the old app. The dialog including the menu and onclick functions are still
 disabled, because of the yet missing auth functions I still have to redo in order to create, edit 
 and delete users from inside the app.
 
## New Fragments and Functions
17.05.2021 00:30
1. About Us
   - Added content to about_us.Fragment. compared to the old one the data is now completely 
   formatted and then displayed via Sites.java
2. utils.Sites.create()
   - Creates a Layout with the given data. Using this class all displayed data will always look the
    same on the ui.
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
    
## Analytics
17.05.2021<br>
Enabled Analytics and put some custom events in the app. I right now just track how often a user is 
clicking on a event in the program fragment. Also logging which sites the user is opening and how 
long the user is staying on them.

## A lot again
18.05.2021 02:05
1. SharedPreferences: 
   - Try to use this method for storing extra data about the user on the device longer than just 
   one session. Don't quite know yet how to format the data, but I will figure something out.
2. Remote Config
   - Enabled remote Config for the current semester. sometimes it still gets the "default" value, 
   but I think, that was just a debug error. 
3. Login
   - Completely redesigned the login and register methods. There are more sites in the dialog now 
   and the code is hopefully easier to read.
4. StartActivity
   - Changed the way the signed in user should get rights. The signed in user has "admin" rights, 
   if its uid is to be found in the current chargen document in the firestore database. also a 
   signed in user has admin rights, if its uid is in the "Editor.super-admin" document.

## A little bit new and many, many comments.
27.05.2021 01:03
1. Remote config:
   - Now using the variables for the donation IBANs and the current semester from RC.
2. Image slider & ImageDownload:
   - Changed the way a watermark gets displayed.
3. Login, LoginDialog
   - Finished the login routines. 
4. ChangeSemesterDialog
   - The design is new. It got a new headline. The user can choose the year and in the year the 
   semester. Depending on the used constructor the Dialog changes the "chosenSemester" or joined 
   semester of the user.
5. Comments
   1. Person.class
      - commented every method, function, constructor and the class itself
   2. Semester.class
      - commented every method, function, constructor and the class itself
   3. News.class
      - commented every method, function, constructor and the class itself
   4. Event.class
      - commented every method, function, constructor and the class itself
   5. Rank.class
      - commented every method, function, constructor and the class itself
   6. SocialMedia.class
      - commented every method, function, constructor and the class itself
   7. CustomFragment.class
      - commented every method, function, constructor and the class itself
   8. OpenExternal.class
      - commented every listener and the interface itself
   9. PictureListener.class
      - commented every listener and the interface itself
6. Crashes
   - because of some changes to the Person.class the app now crashes from time to time. This error 
   has to be found in every class, the data structure in firestore adjusted and some other functions
    have to be adjusted to.
7. TODO
    - solved and added a couple of todos in some classes, the total amount is still the same

## Registration, profile, ana- and crashlytics, design and comments
02.06.2021 19:45<br>
*Version code:* `2_2_020621` *- Version name:* `Alpha 2.2 02.06.2021`<br>
As always some I found some bugs and fixed hopefully all of them. But of course there is more:
1. Added TODOS in classes that aren't commented yet
2. Changed the way I am supposed to write in here
   - max length of version code is 11, so format it like `ReleaseNumber` _ `ReleaseVersion` _ `ddMMyy`
3. Crashlytics
   - made some custom crashlytics events for better understanding why the app crashed.
4. Auth
   - changed some custom listeners
5. Login.class
   - Finished registration methods
6. Analytics
   - added more logging functions
7. Program:
  - now forcing punctuality to be ct/st/whole_day/later/info/title
  - now forcing collar to be io/o/ho
8. MainActivity
  - If a user is logged in his profile picture, name and email will be displayed in the side nav.
9. Profile.class
  - Editing of the users own profile is now possible. Everything except password and email is now
  changeable. For the password the user still has to use login form and select "forgot password".
10. ChangeSemesterDialog
    - can now show a past semester as starting value

## Some new Stuff and more ToDos
05.06.2021 00:00<br>
*Version code:* `2_3_070621` *- Version name:* `Alpha 2.3 07.06.2021`<br>
First of all, I fixed some bugs occurring while registration. <br>
The user can access now the settings page. In it the user can decide what page the one at the start 
of the app shall be. also the user can subscribe to specific event types, not all of them, just some
like i.g. "party", "lecture", "kneipe".<br>
I am thinking about adding some small adds into the app. Like after each month in the program-fragment,
the bottom of event details, every 5-8 news entries and at the bottom of the board. But I have to
check out how that looks. Maybe less. They shouldn't be annoying. But if I do that I have to make a 
new page with privacy policy, terms of use and all that bullshit for a private app. Maybe all that 
crap can be put on a web site which is just linked to by the app.<br>
Also I have to reorganize the "private" parts of the side nav. "internal" and "user management" do
not describe there submenu points well anymore.<br>
Sometimes the side nav does not display the email address of the signed in user. Why??

## Fixed bugs and some more functional fragments
17.06.2021 03:14<br>
*Version code:* `2_4_170621` *- Version name:* `Alpha 2.4 17.06.2021`<br>
- Fixed some bugs which occurred while registering new users.
- Fixed a bug with changing the profile picture
- Changed the formations in this file.
- Added balance Fragment to the side nav of signed in users. In it the current balance of the 
current user is displayed and refreshed in real time.
- Added new function goHome. It can be called from anywhere with a fragment manager. Its purpose is
to send the user back to his in app home screen.