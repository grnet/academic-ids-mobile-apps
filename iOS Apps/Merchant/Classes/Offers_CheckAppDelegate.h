//
//  Offers_CheckAppDelegate.h
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Offers_CheckViewController;
@class Offers_CheckView3Controller;

@interface Offers_CheckAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    Offers_CheckViewController *viewController;
	Offers_CheckView3Controller *view3Controller;
	UINavigationController *navController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet Offers_CheckViewController *viewController;
@property (nonatomic, retain) IBOutlet Offers_CheckView3Controller *view3Controller;
@property (nonatomic, retain) IBOutlet UINavigationController *navController;
@property (nonatomic, retain) NSString *authValue;
@property (nonatomic, retain) NSString *userName;
@property (nonatomic, retain) NSString *pwdMD5Stored;
@property (nonatomic, retain) NSDictionary *offersDict;

-(void)switchToView1:(UIView *) fromView;
-(void)switchToView3:(UIView *) fromView;
-(void)switchToMainMenuView:(UIView *) fromView;

@end

