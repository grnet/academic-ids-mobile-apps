//
//  AcademicID_CheckView3Controller.h
//  Academic ID Inspector
//
//  Created by Michalis Masikos on 15/02/13.
//  Copyright 2013 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface AcademicID_CheckView3Controller : UIViewController <UITextFieldDelegate> {
	NSMutableData *responseData;
	UITextField *existingPWD;
	UITextField	*theNewPWD;
	UITextField	*theNewPWDVerification;
	UIActivityIndicatorView * validationActivityIndicator;
}

@property (nonatomic, retain) IBOutlet UITextField *existingPWD;
@property (nonatomic, retain) IBOutlet UITextField *theNewPWD;
@property (nonatomic, retain) IBOutlet UITextField *theNewPWDVerification;
@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *validationActivityIndicator;
@property (nonatomic, retain) IBOutlet UIScrollView *scrollView;

-(IBAction)stepBack:(id)sender;
-(IBAction)savePWD:(id)sender;
-(IBAction)textFieldDoneEditing:(id)sender;
-(void)clearTextFields;
-(void)showMessage: (NSString *) msg;

@end
