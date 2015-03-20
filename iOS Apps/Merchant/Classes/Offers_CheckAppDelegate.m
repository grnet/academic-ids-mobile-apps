//
//  Offers_CheckAppDelegate.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import "Offers_CheckAppDelegate.h"
#import "Offers_CheckViewController.h"
#import "Offers_CheckView3Controller.h"
#import "Offers_CheckMainMenuViewController.h"


@implementation Offers_CheckAppDelegate

@synthesize window;
@synthesize viewController;
@synthesize view3Controller;
@synthesize navController;
@synthesize authValue;
@synthesize userName;
@synthesize pwdMD5Stored;
@synthesize offersDict;

#pragma mark -
#pragma mark Application lifecycle

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {    
    
    // Override point for customization after application launch.

    // Add the view controller's view to the window and display.
    [self.window addSubview:viewController.view];
    [self.window makeKeyAndVisible];

    return YES;
}


- (void)applicationWillResignActive:(UIApplication *)application {
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, called instead of applicationWillTerminate: when the user quits.
     */
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    /*
     Called as part of  transition from the background to the inactive state: here you can undo many of the changes made on entering the background.
     */
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}


- (void)applicationWillTerminate:(UIApplication *)application {
    /*
     Called when the application is about to terminate.
     See also applicationDidEnterBackground:.
     */
}


#pragma mark -
#pragma mark Memory management

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    /*
     Free up as much memory as possible by purging cached data objects that can be recreated (or reloaded from disk) later.
     */
}


- (void)dealloc {
    [viewController release];
	[view3Controller release];
	[navController release];
	[authValue release];
	[userName release];
	[pwdMD5Stored release];
    [window release];
    [super dealloc];
}

//switch to the login page
-(void)switchToView1:(UIView *)fromView
{
	[fromView removeFromSuperview];
	[window addSubview:viewController.view];
}

//switch to the change pwd page
-(void)switchToView3:(UIView *)fromView
{
	[fromView removeFromSuperview];
	[window addSubview:view3Controller.view];
}

//switch to the main menu page
-(void)switchToMainMenuView:(UIView *)fromView
{
	Offers_CheckMainMenuViewController *mainMenuViewController = [navController.viewControllers objectAtIndex:0];
	if ([fromView isEqual:viewController.view])
	{
		[mainMenuViewController setJustLoggedIn:YES];
	}

	[fromView removeFromSuperview];
	[window addSubview:navController.view];
}

@end
