//
//  AcademicID_CheckViewController.h
//  Academic ID Inspector
//
//  Created by Michalis Masikos on 15/02/13.
//  Copyright 2013 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AcademicID_CheckViewController : UIViewController {
	NSMutableData *responseData;
	UITextField *usernameField;
	UITextField *passwordField;
	UIActivityIndicatorView * loginActivityIndicator;
}

@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *loginActivityIndicator;
@property (nonatomic, retain) IBOutlet UITextField *usernameField;
@property (nonatomic, retain) IBOutlet UITextField *passwordField;
@property (nonatomic, retain) NSDictionary *loadUserDictionary;

-(IBAction)login:(id)sender;
-(IBAction)textFieldDoneEditing:(id)sender;

+(NSString *) md5: (NSString *) input;

@end

