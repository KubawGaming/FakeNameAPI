# FakeNameAPI
<bold>[![](https://jitpack.io/v/KubawGaming/FakeNameAPI.svg)](https://jitpack.io/#KubawGaming/FakeNameAPI)</bold> <strong>Its project version used in gradle/maven</strong>

<br>
A small and simple library based on packets that allows you to easily change players' nicknames and control who sees the changed nickname.

## Example of use:

At the very beginning you need to create an instance of the FakeNameAPI class. Note - make only one instance!

You will not have to save your instance. After creating it, you will have access to FakeNameAPI through the static method.

```java
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // We are creating FakeNameAPI instance and giving Main class (that extends JavaPlugin) as argument
        new FakeNameAPI(this);

        // After creating the instance you are able to get FakeNameAPI using:
        FakeNameAPI fakeNameAPI = FakeNameAPI.getInstance();
    }

}
```

Below are examples of how to operate on the API:

```java
FakeNameAPI fakeNameAPI = FakeNameAPI.getInstance();
Player targetToChangeNick = ...;
Player playerWhoShouldSeeTargetNewName = ...;

// Setting a fake nickname visible to one player:
fakeNameAPI.setFakeName(targetToChangeNick, "Anonymous", playerWhoShouldSeeTargetNewName);

// Setting a fake nickname visible for all players
fakeNameAPI.setFakeName(targetToChangeNick, "Anonymous", Bukkit.getOnlinePlayers());

// Checking someone's fake nickname:
if(fakeNameAPI.getFakeNames().containsKey(targetToChangeNick)) {
    // Code if the target has a fake nickname set
} else {
    // Code if the target hasn't a fake nickname set
}

// Getting a nickname that a player sees in another player
// if `playerWhoShouldSeeTargetNewName` does not see a fake nick, this method will return the real nick targetToChangeNick
fakeNameAPI.getSeeingNick(playerWhoShouldSeeTargetNewName, targetToChangeNick);

// If for `player A` you want to reset the fake nickname he sees in `player B`, you can do it like this:
fakeNameAPI.unsetFakeName(targetToChangeNick, playerWhoShouldSeeTargetNewName);

// And this is how you can remove the fake nickname:
// (All players who have seen a player's fake nickname will see the player's real nickname after using this)
fakeNameAPI.resetName(targetToChangeNick);
```

## Gradle:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KubawGaming:FakeNameAPI:VERSION_HERE'
}
```

## Maven:

```html
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.KubawGaming</groupId>
    <artifactId>FakeNameAPI</artifactId>
    <version>VERSION_HERE</version>
</dependency>
```
