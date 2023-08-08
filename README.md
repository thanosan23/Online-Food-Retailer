# Online-Food-Retailer


## Product Description
Online food retail can be hugely beneficial for farmers, offering a much higher share of the
retail price and giving shoppers more transparency so they can choose food that better
aligns with their values. It enables farmers that farm sustainably to find shoppers and a fair
price, which makes sustainable farming worthwhile. Agriculture is one of the biggest
contributors to ecological degradation globally, so it is crucially important that we find ways to
bring more farmers into sustainable food production.

Since the pandemic the number of online platforms to sell locally produced or sustainable
food has exploded. There is a huge competition in online platforms, each with relatively low
volume in comparison to conventional supermarkets and routes to market. This means that if
farmers want to benefit from these sales platforms, they need to upload their produce and
maintain their stock levels on more than one of them, often 4 or 5. This is time consuming for
farmers and unwanted admin.

The Data Food Consortium is working to solve this problem. Working for dozens of food
retail platforms they are building a data infrastructure that means that product and stock data
can be shared between platforms seamlessly.
This project is to create an app specifically for farmers, that interfaces with the Data Food
Consortium API and will enable farmers to upload their produce ready to be sold through any
platform on the network.
- The primary user group will be those directly producing food products and wanting to
access direct retail. These might be farmers, bakers, growers, butchers, jam makers etc etc.
We’ll call these users Producers.
- The app will interface with the Data Food Consortium standard (Gitbook, examples). Using
this standard the app will be able to connect to a number of platforms for food sales. In this
project you will be testing your integration via the DFC testing tool. Details of how to access
this will be shared when on selection of the project.
- Producers need to be able to create, read, update and delete (CRUD) their products. As a
minimum producers will need to be able to create and modify:
- Product names
- Product description
- Product images
- Price
- Stock available
Any other fields that can be handled via the DFC standard can also be handled in the app.
Consider the user experience if you choose to add many fields.
- Producers should be able to connect their products to multiple selling platforms via the app.
This means that:
﹣ Producers will need to be able to view platforms that are available via the app
﹣ Producers will be able to connect specific products to different platforms and set
stock and price levels on the different platforms.


## Build/Compile Instructions

1. Install Android Studio (https://developer.android.com/studio) 

2. Please clone this GitHub repository at https://github.com/thanosan23/Online-Food-Retailer and open up the project on Android Studio.

3. Create a FireBase database on Firebase console. Click add project, and name the project. Be sure to enable the “Android” option when creating the project to let Firebase know that the app is an Android application

4. After you select Android, you will come across a form to set up the application. In the Android package name, write “ca.uwaterloo.cs”.

5. For Debug Signing Certificate SHA-1 and SHA-256, on Android Studio, click on Gradle at the right-most menu bar on Android Studio. Click on the Gradle icon (looks like an elephant). A screen will pop up. Type “gradle signingreport”. This will open up a screen with a SHA-1 Key and a SHA-256 Key. Copy and paste both keys and enter it into the Firebase form.

6. As instructed by Firebase, download “google-services.json” and move the file into the app folder.

7. You can skip adding the Firebase SDK instructions as the GitHub repository has already set up the Firebase SDK.

8. When you are on Firebase, go to project categories, then build, and select authentication. Click “Get Started” and then toggle the Enable bar, and click “Save”.

9. Go back to the console and go to project categories, then build, and select a realtime database. Click “Create Database”, and then click “Start in test mode”. Click “Enable”.

11. On the realtime database, go to the "rules" section. Paste in the following:
```json
{
  "rules": {
    ".read": "auth.uid !== null",
    ".write": "auth.uid !== null"
  }
}
```
This only allows authenticated users to be able to add their products, add harvests, and add stores to the database.

12. Now that Firebase is set up, click the play button on Android Studio and the emulator should start up and the app should open!

13. To test out the application on an Android app, on the immediate left of the play button, there is a drop down menu. Click the drop down menu and select  “Pair Devices using Wi-Fi”. Follow the instructions given by Android Studio to pair your device.

Note: When creating a signed APK, a keystore will be generated. We need to add the keystore's SHA fingerprints to Firebase! This can be done using keytool: `keytool -list -v -keystore <location of keystore> -alias <name of key alias>`. Keep in mind that to use `keytool`, you do need Java installed in your system. After you run the command, you need to add the provided SHA-1 and SHA-256 fingerprints to your Firebase app in the Firebase console!

## Installation Instructions

1. To download the app, please go to this GitHub repository, click `app/release/app-release.apk` and download the APK file to your android device (this can be done by clicking view raw).

2. Go to the “Files” app on your android device and click the downloaded APK file. This should download the apk file to your device.

3. If you do get a warning saying that the app is unsafe, click “Install Anyway”.

4. Once the installation process is done, the app should be downloaded to your phone!

## User Interview

The below is an excerpt from an interview with a food producer and potential user of the app.
What do you produce?

I farm on 200 acres in (wherever). I produce mostly salad, lettuce and field scale veg for a
local wholesaler.

Tell me about how you sell food at the moment?

About 90% of what I produce goes to the wholesaler. I get about 30c from the dollar from
them. The last 10% goes into a local veg box and a local food hub. From the food hub I get
80c from the dollar. From the veg box I get 75%. By far this is the best route to market I have
and if I could sell everything like this it would be ideal. But the hub is too small. But even now
I spend more time selling the 10% through the local schemes than I do everything else
through the wholesaler.

What do you like about the smaller outlets?

That I get more of the sale price. It’s really amazing. If I was selling everything through those
outlets I’d be able to really invest in the farm. I want to invest in more wildlife corridors in the
unused areas but without grants I can’t pay for that through farming alone. Well, I could if I
could sell everything through small outlets. And my partner could quit their second job and
have more time on the farm too.

What is difficult about the smaller outlets?

Like I say I spend more time selling through the small outlets than I do the wholesaler, but I
sell 9 times as much through the wholesaler. I constantly have to keep everything updated
across platforms. And it's hard to update things. I’ll be out on the field and know how much I
can harvest next week. Then I have to input into the system when I get back to my desk.
And often I can’t read my own notes. Then the prices change and I have to manually check
the prices of everyone else on the platforms to make sure I set the price right. With the
wholesaler I just speak to them on the phone once a week and say ‘Sal, this is what I’ve got’
and then they just come pick it up and pay me. It’s so much easier that way.
What would make it easier to sell through these outlets?
If I could just put my produce online in one place and have it sell then it’d be so much easier.
It’d be great if I could do it from my phone. Even when it was wet and my fingers were dirty.
It needs to be easy, even from big, wet, dirty fingers.

Anything else you’d like to add?

Hrm, maybe that I have a few workers and it’s important that many of us can manage the
account. Just needs to be one account, but if it has to be done from my phone that’ll make
things really hard.
