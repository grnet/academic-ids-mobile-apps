//
//  Offers_CheckMainMenuViewController.h
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 27/03/2014.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface Offers_CheckMainMenuViewController : UIViewController {
	NSArray *controllers;
	NSMutableData *responseData;
	UIActivityIndicatorView * validationActivityIndicator;
	BOOL justLoggedIn;
	IBOutlet UIToolbar *toolBar;
}

@property (nonatomic, retain) NSArray *controllers;
@property (nonatomic, assign) BOOL justLoggedIn;
@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *validationActivityIndicator;

-(IBAction)refresh:(id)sender;
-(IBAction)viewSettings:(id)sender;
-(NSString *)getCurrentTime;
-(void)logoutProcess;
-(void)getOffers;

@end
