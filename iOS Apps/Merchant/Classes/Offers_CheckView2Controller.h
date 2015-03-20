//
//  AcademicID_CheckView2Controller.h
//  AcademicID Check
//
//  Created by Michalis Masikos on 11/23/12.
//  Copyright 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZBarSDK.h"

@interface Offers_CheckView2Controller : UIViewController <UITextFieldDelegate, ZBarReaderDelegate> {
	NSMutableData *responseData;
	UITextField *barcodeIDField;
	NSDictionary *resultsViewDictionary;
	UIActivityIndicatorView * validationActivityIndicator;
	ZBarReaderViewController *reader;
	IBOutlet UIScrollView *scrollView;
}

@property (nonatomic, retain) IBOutlet UITextField *barcodeIDField;
@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *validationActivityIndicator;
@property (nonatomic, retain) IBOutlet UILabel *resultLabel;
@property (nonatomic, retain) IBOutlet UILabel *barcodeIDLabel;
@property (nonatomic, retain) IBOutlet UILabel *nameLabel;
@property (nonatomic, retain) IBOutlet UITextView *statusTextView;
@property (nonatomic, retain) IBOutlet UITextView *homeAddressTextView;
@property (nonatomic, retain) IBOutlet UITextView *uniAddressTextView;


-(IBAction)logout:(id)sender;
-(IBAction)checkID:(id)sender;
-(IBAction)backgroundTap:(id)sender;
-(IBAction)scanTap:(id)sender;
-(IBAction)viewSettings:(id)sender;
-(UIView *)overlayView;
-(void)fillTable:(NSDictionary *)responseDict;


@end
