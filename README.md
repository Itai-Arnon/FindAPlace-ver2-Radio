

### Description
This project is a comprehensive mobile application designed to enhance the travel experience by providing detailed touristic information and advanced map navigation. It helps users discover local attractions, restaurants, pubs, and other points of interest.

### Key Features
- **Touristic Information**: Offers detailed information about various tourist attractions including historical sites, museums, parks, and more.
- **Restaurant and Pub Recommendations**: Suggests top-rated restaurants, pubs, and cafes nearby based on user preferences and location.
- **Map Navigation**: Integrates with map services to provide accurate navigation and directions to selected destinations.
- **User Interface**: Utilizes multiple activities and fragments to create a seamless and interactive user experience.
- **Local Storage**: Implements SQLite database to store and manage user data, preferences, and search history.
- **Content Providers**: Uses content providers for efficient data sharing between different parts of the application.
- **Additional Features**: Includes options for user reviews, ratings, and saving favorite places for future reference.

### Technologies Used
- **Programming Languages**: Java-Android/XML
- **Database**: SQLite
- **APIs**: Google Maps API for navigation and location services
- **Frameworks**: Android SDK
- **Development Tools**: Android Studio



### Future Enhancements
- **User Authentication**: Adding login functionality to personalize user experience and sync data across devices.
- **Offline Mode**: Enabling offline access to touristic information and saved maps.
- **Social Sharing**: Allowing users to share their favorite places and itineraries on social media platforms.

This project aims to make exploring new cities and finding great places to eat and visit easier and more enjoyable for travelers. By combining comprehensive touristic information with reliable map navigation, it provides a one-stop solution for all travel needs.# FindAPlace-ver2

FindAplace With additional radio buttons
An android app written in Java, that provides touristic information. Uses multiple activities, fragments, sql-lite, content providers and more

This project has taught me a lot. A lot about programming in Android. I faced some fierce bugs. It took me a while to overcome them. My biggest truimph was figuring out I should use a singleton.  I had to keep data, that was always reset due to the Activty end cycle. Finally I found out that the application class doesn't destructs itself, that's it's a singleton and I used it accordingly. 

