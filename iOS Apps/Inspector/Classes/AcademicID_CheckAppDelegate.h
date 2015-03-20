//
//  AcademicID_CheckAppDelegate.h
//  Academic ID Inspector
//
//  Created by Michalis Masikos on 15/02/13.
//  Copyright 2013 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class AcademicID_CheckViewController;
@class AcademicID_CheckView2Controller;
@class AcademicID_CheckView3Controller;

@interface AcademicID_CheckAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    AcademicID_CheckViewController *viewController;
	AcademicID_CheckView2Controller *view2Controller;
	AcademicID_CheckView3Controller *view3Controller;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet AcademicID_CheckViewController	*viewController;
@property (nonatomic, retain) IBOutlet AcademicID_CheckView2Controller	*view2Controller;
@property (nonatomic, retain) IBOutlet AcademicID_CheckView3Controller	*view3Controller;
@property (nonatomic, retain) NSString *authValue;
@property (nonatomic, retain) NSString *userName;
@property (nonatomic, retain) NSString *pwdMD5Stored;

-(void)switchToView1:(UIView *) fromView;
-(void)switchToView2:(UIView *) fromView;
-(void)switchToView3:(UIView *) fromView;


@end

