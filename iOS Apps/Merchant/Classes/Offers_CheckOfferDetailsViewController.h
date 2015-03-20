//
//  Offers_CheckOfferDetailsViewController.h
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZBarSDK.h"


@interface Offers_CheckOfferDetailsViewController : UIViewController <UIAlertViewDelegate, ZBarReaderDelegate> {
	UITextField *startDateLabel;
	UITextField *endDateLabel;
	UITextView *descriptionTextView;
	UITextView *criteriaTextView;
	UIActivityIndicatorView *validationActivityIndicator;
	
	NSString *description;
	NSString *criteria;
	NSString *startDate;
	NSString *endDate;
	
	ZBarReaderViewController *reader;
	
	UITextField *serialTextField;
	NSString *serialString;
	
	NSMutableData *responseData;
	NSDictionary *offerDetailsDict;
}

@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *validationActivityIndicator;
@property (nonatomic, retain) IBOutlet UITextField *startDateTextField;
@property (nonatomic, retain) IBOutlet UITextField *endDateTextField;
@property (nonatomic, retain) IBOutlet UITextView *descriptionTextView;
@property (nonatomic, retain) IBOutlet UITextView *criteriaTextView;
@property (nonatomic, copy)	NSString *description;
@property (nonatomic, copy)	NSString *criteria;
@property (nonatomic, copy)	NSString *startDate;
@property (nonatomic, copy)	NSString *endDate;
@property (nonatomic, copy) NSDictionary *offerDetailsDict;

-(IBAction)scanTap:(id)sender;
-(UIView *)overlayView;
-(void)backBtnAction;
-(void)checkID:(NSString *)serial;
-(void)showMessage:(NSString *) msg withTitle:(NSString *) titleStr withCancelButtonTitle:(NSString *)cancelBtnTitle;

@end
