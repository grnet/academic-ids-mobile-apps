//
//  Offers_CheckOfferDetailsViewController.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//


#import "Offers_CheckOfferDetailsViewController.h"
#import <QuartzCore/QuartzCore.h> 
#import "ScanOverlayViewController.h"
#import "Offers_CheckAppDelegate.h"
#import "YAJLiOS/YAJL.h"


@implementation Offers_CheckOfferDetailsViewController
@synthesize startDateTextField;
@synthesize endDateTextField;
@synthesize descriptionTextView;
@synthesize criteriaTextView;
@synthesize validationActivityIndicator;
@synthesize startDate;
@synthesize endDate;
@synthesize description;
@synthesize criteria;
@synthesize offerDetailsDict;


#pragma mark -
#pragma mark Barcode scanner


-(IBAction)scanTap:(id)sender { 
	// ADD: present a barcode reader that scans from the camera feed
	reader = [ZBarReaderViewController new];
    reader.readerDelegate = self;
    reader.supportedOrientationsMask = ZBarOrientationMask(UIInterfaceOrientationPortrait); //ZBarOrientationMaskAll;
	reader.tracksSymbols = TRUE;
	reader.readerView.autoresizingMask = TRUE;
	reader.showsZBarControls = FALSE;
	//reader.showsCameraControls = TRUE;
	reader.cameraOverlayView = [self overlayView];
	
    ZBarImageScanner *scanner = reader.scanner;
    // TODO: (optional) additional reader configuration here
	
    // EXAMPLE: disable rarely used I2/5 to improve performance
    [scanner setSymbology: ZBAR_I25
				   config: ZBAR_CFG_ENABLE
					   to: 0];
	
    // present and release the controller
    [self presentModalViewController: reader animated: YES];

    [reader release];
	
}


-(UIView *)overlayView
{
    CGFloat ovewrlayViewHeight = 436;
	
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
    {
        CGSize result = [[UIScreen mainScreen] bounds].size;
        if(result.height == 568)
        {
            // iPhone 5
            ovewrlayViewHeight = 524;
        }
    }
    
	UIView *view=[[UIView alloc] initWithFrame:CGRectMake(0, ovewrlayViewHeight, 320, 44)];
	[view setBackgroundColor:[UIColor clearColor]];
	UIToolbar *myToolBar = [[UIToolbar alloc] init];
	myToolBar.tintColor = [UIColor colorWithRed: .5 green: 0 blue: 0 alpha: 1];
	UIBarButtonItem *button=[[UIBarButtonItem alloc] initWithTitle:@"Πίσω" style:UIBarButtonItemStyleBordered target:self action:@selector(backBtnAction)];
	
	[myToolBar setItems:[NSArray arrayWithObjects:button,nil]];    
	[myToolBar setBarStyle:UIBarStyleDefault];
	CGRect toolBarFrame;
	toolBarFrame = CGRectMake(0, 0, 320, 44);
	[myToolBar setFrame:toolBarFrame];
	[view addSubview:myToolBar];
	
	[button release];
	[myToolBar release];
	
	return  view;
}


-(void)backBtnAction
{
    [reader dismissModalViewControllerAnimated: YES];
}


- (void) imagePickerController: (UIImagePickerController*) zBarReader didFinishPickingMediaWithInfo: (NSDictionary*) info
{
    // ADD: get the decode results
    id<NSFastEnumeration> results = [info objectForKey: ZBarReaderControllerResults];
    ZBarSymbol *symbol = nil;
    for(symbol in results)
        // just grab the first barcode
        break;
	
    // dismiss the controller
    [zBarReader dismissModalViewControllerAnimated: YES];

	[self checkID:symbol.data];
}


#pragma mark -
#pragma mark Check Serial


-(void)checkID:(NSString *)serial
{
	if ([serial length] == 0) {
		[self showMessage:@"Δεν έχετε εισάγει Σειριακό Αριθμό!" withTitle:@"Προσοχή" withCancelButtonTitle:@"ΟΚ"];
	}
	else if ([serial length] < 12) {
		[self showMessage:@"Έχετε εισάγει λιγότερα από 12 ψηφία!" withTitle:@"Προσοχή" withCancelButtonTitle:@"ΟΚ"];
	}
	else if ([serial length] > 12) {
		[self showMessage:@"Έχετε εισάγει περισσότερα από 12 ψηφία!" withTitle:@"Προσοχή" withCancelButtonTitle:@"ΟΚ"];
	}	
	else 
	{
		responseData = [[NSMutableData data] retain];

		NSString *urlString = [[NSString alloc] initWithFormat:@"%@inspectProviderOffer",appBaseURL];
		
		NSLog(@"urlString=%@",urlString);
		
		[self.navigationController.view setUserInteractionEnabled:NO];  
		[validationActivityIndicator startAnimating];
		
		NSMutableURLRequest *mutableRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData
																  timeoutInterval:12];
		
		[urlString release];
		
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		
		[mutableRequest setHTTPMethod:@"POST"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"CONTENT-TYPE"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"ACCEPTS"];
		[mutableRequest setValue:[appDelegate authValue] forHTTPHeaderField:@"AUTHORIZATION"];
		
		NSLog(@"HTTP request headers: %@", [mutableRequest allHTTPHeaderFields]);
		
		NSMutableDictionary *bodyDictionary = [[NSMutableDictionary alloc] initWithDictionary:self.offerDetailsDict copyItems:YES];
		[bodyDictionary setValue:serial forKey:@"academicID"];
		
		NSString *bodyJSONString = [bodyDictionary yajl_JSONString];
		
		NSMutableData *body = [NSMutableData data];
		[body appendData:[bodyJSONString dataUsingEncoding:NSUTF8StringEncoding]];
		[mutableRequest setHTTPBody:body];
		
		[bodyDictionary release];
		
		[[NSURLConnection alloc] initWithRequest:mutableRequest delegate:self];	
	}
}


- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[responseData setLength:0];
}


- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[responseData appendData:data];
}


- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	[connection release];
	[responseData release];
	
	[validationActivityIndicator stopAnimating];
	[self.navigationController.view setUserInteractionEnabled:YES];  
	
	[self showMessage:@"Υπάρχει πρόβλημα στη σύνδεση δικτύου!" withTitle:@"Προσοχή" withCancelButtonTitle:@"ΟΚ"];
	
	NSLog(@"Connection failed: %@", [error description]);
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	[connection release];	
	
	[validationActivityIndicator stopAnimating];
	[self.navigationController.view setUserInteractionEnabled:YES];  
	
	NSString *responseString = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
	
	NSLog(@"responsestring=%@",responseString);
	
	NSDictionary *responseDictionary = [responseString yajl_JSON];
	
	if ([[responseDictionary valueForKey:@"response"] isEqualToString:@"SUCCESS"])
	{
		NSDictionary *inspectionResult = [responseDictionary objectForKey:@"inspectionResult"];
		
		if ([[inspectionResult objectForKey:@"valid"] boolValue] == YES)
		{
			[self showMessage:[NSString stringWithFormat:@"%@%@%@",@"Ο χρήστης με σειριακό ",[inspectionResult valueForKey:@"academicId"],@" δικαιούται την προσφορά!"]
					withTitle:nil withCancelButtonTitle:@"ΟΚ"];
		}
		else {
			[self showMessage:[inspectionResult objectForKey:@"error"] withTitle:nil withCancelButtonTitle:@"ΟΚ"];
		}
	}
	else {
		[self showMessage:[responseDictionary objectForKey:@"errorReason"] withTitle:@"Προσοχή" withCancelButtonTitle:@"ΟΚ"];
	}
	
	
	[responseData release];
	[responseString release];
}


- (IBAction)readID:(id)sender 
{
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Παρακαλώ εισάγετε το" message:nil
												   delegate:self cancelButtonTitle:@"ΑΚΥΡΟ" otherButtonTitles:@"ΕΥΡΕΣΗ", nil];
	
    alert.alertViewStyle = UIAlertViewStylePlainTextInput;
    serialTextField = [alert textFieldAtIndex:0];
    
    serialTextField.text = @"";
    serialTextField.clearButtonMode = UITextFieldViewModeWhileEditing;
    serialTextField.keyboardType = UIKeyboardTypeNumberPad;
    [serialTextField setPlaceholder:@"Σειριακό Αριθμό"];
    
	[alert show];
	[alert release];
}


-(void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
	if (buttonIndex == 1) 
	{
		serialString = serialTextField.text;
		[self checkID:serialString];
	}
}


-(void)showMessage:(NSString *)msg withTitle:(NSString *)titleStr withCancelButtonTitle:(NSString *)cancelBtnTitle
{
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:titleStr message:msg
												   delegate:nil cancelButtonTitle:cancelBtnTitle otherButtonTitles:nil];
	
	[alert show];		
	[alert release];
}


#pragma mark -
#pragma mark View lifecycle


- (void)viewDidLoad {
	self.descriptionTextView.layer.cornerRadius = 7.0;
	self.descriptionTextView.clipsToBounds = YES;
	[descriptionTextView.layer setBorderColor:[[[UIColor grayColor] colorWithAlphaComponent:0.5] CGColor]];
	[descriptionTextView.layer setBorderWidth:2.0];
	[descriptionTextView.layer setMasksToBounds:YES];
	[descriptionTextView flashScrollIndicators];
	    
	self.criteriaTextView.layer.cornerRadius = 7.0;
	self.criteriaTextView.clipsToBounds = YES;
    [criteriaTextView.layer setBorderColor:[[[UIColor grayColor] colorWithAlphaComponent:0.5] CGColor]];
	[criteriaTextView.layer setBorderWidth:2.0];
	[criteriaTextView.layer setMasksToBounds:YES];
	[criteriaTextView flashScrollIndicators];
	
	//create the serialInspection button
	UIBarButtonItem *inspectSerialButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(readID:)];
	self.navigationController.topViewController.navigationItem.rightBarButtonItem = inspectSerialButton;
	
	[inspectSerialButton release];
	
	[super viewDidLoad];
}


- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
}


- (void)viewWillAppear:(BOOL)animated
{
	startDateTextField.text = startDate;
	endDateTextField.text = endDate;
	descriptionTextView.text = description;
	criteriaTextView.text = criteria;
    
    NSLog(@"description=%@", description);
	[super viewWillAppear:animated];
}


- (void)viewDidUnload {
	self.startDateTextField = nil;
	self.endDateTextField = nil;
	self.descriptionTextView = nil;
	self.criteriaTextView = nil;
	self.startDate = nil;
	self.endDate = nil;
	self.description = nil;
	self.criteria = nil;
	self.offerDetailsDict = nil;
	
    [super viewDidUnload];
}


- (void)dealloc {
	[startDateTextField release];
	[endDateTextField release];
	[descriptionTextView release];
	[criteriaTextView release];
	[validationActivityIndicator release];
	[startDate release];
	[endDate release];
	[description release];
	[criteria release];
	[offerDetailsDict release];
	
    [super dealloc];
}


@end
