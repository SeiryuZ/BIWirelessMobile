# Frequently Asked Question


## General stuff

#### Q: What is this class about?
A: Mobile programming


#### Q: What language? platform?
A: Java, Android.

#### Q: What about swift / iOS?
A: It's hard to make sure everyone have access to a mac. If you want to ask learn / ask about swift I can help you, but for the purpose of the class, we will use Java/android exclusively


===
## Final project

#### Q: What library/framework/sass/baas/etc you recommend for ... ?

##### 1.Image
  * Glide (https://github.com/bumptech/glide)

##### 2. Database
  * SQLite (https://developer.android.com/reference/android/database/sqlite/package-summary.html)
  * Realm (https://realm.io/products/realm-mobile-database/)

##### 3. Design
  * Material design (https://developer.android.com/design/material/index.html)


#### Q: What are some interesting encryption scheme to securely transmit stuff?
A: There are many schemes, but I would suggest to learn about Android's keystore system and how you can use that to encrypt stuff. https://developer.android.com/training/articles/keystore.html 

You can use the keys generated to build a universal public key list for your application and other user can send encrypted messages that only intended users can read. For more info see - [Public Key Crytography](https://en.wikipedia.org/wiki/Public-key_cryptography)

#### Q: Can we use third party libraries ? (i.e, tensorflow, Glide, butterknife)
A: Yes, it is actually encouraged to use third party libraries to help with your development. Please do maintain a list of third party libraries and put attribution when appropriate(See their LICENSE)

#### Q: Can we use BaaS or do we have to create our own backend ? (i.e, firebase)
A: For this class, I will only concerns myself with regards to the mobile application. Backends will not be used as the main basis for the marking. Feel free to use BaaS

#### Q: What do you mean by peer review?
A: During the project phases, there will be an allocated time for teams to review each others work. This peer review activity will be used to derive class participation marks

#### Q: Can I develop game for the final project?
A: On the assessment rubric, there are components that measures how well students integrate concepts learned during class sessions into the project. As mobile game development usually utilize game engines that does not apply the concepts I am going to teach, for the purpose of this class, **I am going to reject game project**
