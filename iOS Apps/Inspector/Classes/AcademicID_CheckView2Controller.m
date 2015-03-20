//
//  AcademicID_CheckView2Controller.m
//  Academic ID Inspector
//
//  Created by Michalis Masikos on 15/02/13.
//  Copyright 2013 __GRNET__. All rights reserved.
//

#import "AcademicID_CheckView2Controller.h"
#import "AcademicID_CheckAppDelegate.h"
#import <YAJLiOS/YAJL.h>

@implementation AcademicID_CheckView2Controller
@synthesize barcodeIDField;
@synthesize validationActivityIndicator;
@synthesize resultLabel;
@synthesize barcodeIDLabel;
@synthesize nameLabel;
@synthesize statusTextView;
@synthesize homeAddressTextView;
@synthesize uniAddressTextView;


#pragma mark -
#pragma mark Main menu functionality


-(IBAction)logout:(id)sender
{
	barcodeIDField.text = nil;
	scrollView.hidden = YES;
	
	AcademicID_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	[appDelegate switchToView1:self.view];
	[appDelegate setAuthValue:nil];
	[appDelegate setUserName:nil];
	[appDelegate setPwdMD5Stored:nil];
}

-(IBAction)viewSettings:(id)sender
{
	
	AcademicID_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	[appDelegate switchToView3:self.view];
}

-(IBAction)checkID:(id)sender
{
    [self generateCheckIDRequest:[barcodeIDField text]];
}


-(void)generateCheckIDRequest:(NSString *)serial
{
    //fills the barcodeIDField with the value of the scanned QR code
    [barcodeIDField setText:serial];
    
    if ([[barcodeIDField text] length] == 0) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Δεν έχετε εισάγει Σειριακό Αριθμό!"
													   delegate:nil cancelButtonTitle:@"Κλείσιμο" otherButtonTitles:nil];
		
		[alert show];
		[alert release];
	}
	else if ([[barcodeIDField text] length] < 12) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Έχετε εισάγει λιγότερα από 12 ψηφία!"
													   delegate:nil cancelButtonTitle:@"Κλείσιμο" otherButtonTitles:nil];
		
		[alert show];
		[alert release];
	}
	else {
		[barcodeIDField resignFirstResponder];
		responseData = [[NSMutableData data] retain];
		
        NSString *urlString = [[NSString alloc] initWithFormat:@"%@inspectAcademicID",appBaseURL];
		
        NSLog(@"urlString=%@",urlString);
		
		[self.view setUserInteractionEnabled:NO];
		[validationActivityIndicator startAnimating];
		
        
		NSMutableURLRequest *mutableRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData
																  timeoutInterval:12];
		
		[urlString release];
		
		AcademicID_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
        
		[mutableRequest setHTTPMethod:@"POST"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"CONTENT-TYPE"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"ACCEPTS"];
		[mutableRequest setValue:[appDelegate authValue] forHTTPHeaderField:@"AUTHORIZATION"];
		
		NSLog(@"HTTP request headers: %@", [mutableRequest allHTTPHeaderFields]);
		
		NSDictionary *bodyDictionary = [NSDictionary dictionaryWithObject:serial forKey:@"SubmissionCode"];
		NSString *bodyJSONString = [bodyDictionary yajl_JSONString];
		
		NSMutableData *body = [NSMutableData data];
		[body appendData:[bodyJSONString dataUsingEncoding:NSUTF8StringEncoding]];
		[mutableRequest setHTTPBody:body];
		
		[[NSURLConnection alloc] initWithRequest:mutableRequest delegate:self];
	}
}


-(IBAction)backgroundTap:(id)sender { 
	[barcodeIDField resignFirstResponder];
}


-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    //limit the size :
    int limit = 12;
	if ([textField.text length]>=limit && [string length] > range.length) {
		
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Έχετε ήδη εισάγει 12 ψηφία!"
													   delegate:nil cancelButtonTitle:@"Κλείσιμο" otherButtonTitles:nil];
		
		[alert show];
		[alert release];
	}
	
    return !([textField.text length]>=limit && [string length] > range.length);
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
	[self.view setUserInteractionEnabled:YES];
	
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Προσοχή" message:@"Υπάρχει πρόβλημα στη σύνδεση δικτύου!"
												   delegate:nil cancelButtonTitle:@"ΟΚ" otherButtonTitles:nil];
	
	[alert show];
	[alert release];
	
	NSLog(@"Connection failed: %@", [error description]);
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	[connection release];
	
	[validationActivityIndicator stopAnimating];
	[self.view setUserInteractionEnabled:YES];
	
    NSString *responseString = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
	
	NSLog(@"responsestring=%@",responseString);
	
	NSDictionary *responseDictionary = [responseString yajl_JSON];
	if ([[responseDictionary objectForKey:@"response"] isEqualToString:@"SUCCESS"])
	{
		[self fillTable:responseDictionary];
		scrollView.hidden = NO;
	}
	else {
        barcodeIDField.text = nil;
        scrollView.hidden = YES;

		AcademicID_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		[appDelegate setAuthValue:nil];
		[appDelegate setUserName:nil];
		[appDelegate setPwdMD5Stored:nil];
        [appDelegate switchToView1:self.view];
	}
	
	[responseData release];
	[responseString release];
}


- (BOOL)textFieldShouldClear:(UITextField *)textField
{
	if (textField == self.barcodeIDField)
		scrollView.hidden = YES;
	return YES;
}


- (void)fillTable:(NSDictionary *)responseDict
{
	NSDictionary *resultsDict = [responseDict objectForKey:@"inspectionResult"];
	
	NSString *statusString = @"ΑΔΥΝΑΜΙΑ ΕΛΕΓΧΟΥ";
	NSString *statusDetailString = @"";
    NSString *nameString = @"";
    NSString *homeAddressString;
    NSString *uniAddressString;
    NSString *validationErrorString;
    [self.resultLabel setTextColor:[UIColor redColor]];
    
    //fill the home address String
    if (![[resultsDict objectForKey:@"residenceLocation"] isEqual:[NSNull null]])
        homeAddressString = [resultsDict objectForKey:@"residenceLocation"];
    else
        homeAddressString = @"";
    
    //fill the university address String
    if (![[resultsDict objectForKey:@"universityLocation"] isEqual:[NSNull null]])
        uniAddressString = [resultsDict objectForKey:@"universityLocation"];
    else
        uniAddressString = @"";
    
    //fill the validation error String
    if (![[resultsDict objectForKey:@"validationError"] isEqual:[NSNull null]])
        validationErrorString = [resultsDict objectForKey:@"validationError"];
    else
        validationErrorString = @"";
    
    //fill the name String
    if (![[resultsDict objectForKey:@"greekFirstName"] isEqual:[NSNull null]]) {
        nameString = [NSString stringWithFormat:@"%@%@",[resultsDict objectForKey:@"greekFirstName"],[resultsDict objectForKey:@"greekLastName"]];
    }
    else if (![[resultsDict objectForKey:@"latinFirstName"] isEqual:[NSNull null]])
    {
        nameString = [NSString stringWithFormat:@"%@%@",[resultsDict objectForKey:@"latinFirstName"],[resultsDict objectForKey:@"latinLastName"]];
    }

    //Deltio Eidikoy Eisitiriou is valid
	if (![[resultsDict objectForKey:@"pasoValidity"] isEqual:[NSNull null]])
    {
        if ([[resultsDict objectForKey:@"pasoValidity"] isEqualToString:@"Ναι"])
        {
            statusString = @"ΕΓΚΥΡΟ";
            statusDetailString = @"";
            [self.resultLabel setTextColor:[UIColor greenColor]];
        }
        else if ([[resultsDict objectForKey:@"pasoValidity"] isEqualToString:@"Όχι"])
        {
            statusString = @"ΑΚΥΡΟ";
            statusDetailString = [resultsDict objectForKey:@"cancellationReason"];
        }
    }

    //Deltio Eidikoy Eisitiriou is invalid
    if (([[resultsDict objectForKey:@"webServiceSuccess"] intValue] == FALSE) &&([validationErrorString isEqualToString:@"Δεν βρέθηκε αίτηση με το 12ψήφιο κωδικό που εισάγατε"] || [validationErrorString isEqualToString:@"Ο 12ψήφιος που εισάγατε δεν αντιστοιχεί σε αίτηση φοιτητή"] || [validationErrorString isEqualToString:@"Η Ακαδημαϊκή Ταυτότητα δεν έχει υποβληθεί οριστικά από το φοιτητή"] || [validationErrorString isEqualToString:@"Η Ακαδημαϊκή Ταυτότητα έχει απορριφθεί από τη γραμματεία"]))
    {
        statusString = @"ΑΚΥΡΟ";
		statusDetailString = validationErrorString;
    }

    //if Deltio Eidikoy Eisitiriou is not valid/invalid, then a system error occurred
    
	[self.resultLabel setText:statusString];
	[self.barcodeIDLabel setText:[resultsDict objectForKey:@"academicId"]];
	[self.nameLabel setText:nameString];
	[self.statusTextView setText:statusDetailString];
	[self.homeAddressTextView setText:homeAddressString];
	[self.uniAddressTextView setText:uniAddressString];
	
	[scrollView setContentOffset:CGPointZero animated:NO];
}


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
    //reader.readerView.zoom = 1.0;
    
    // present and release the controller
    [self presentModalViewController: reader animated: YES];
    
    [reader release];
}


-(UIView *)overlayView
{
    CGRect applicationFrame = [[UIScreen mainScreen] applicationFrame];
	UIView *view=[[UIView alloc] initWithFrame:applicationFrame]; 
	[view setBackgroundColor:[UIColor clearColor]];
	UIToolbar *myToolBar = [[UIToolbar alloc] init];
	myToolBar.tintColor = [UIColor colorWithRed: .5 green: 0 blue: 0 alpha: 1];
	UIBarButtonItem *button=[[UIBarButtonItem alloc] initWithTitle:@"Πίσω" style:UIBarButtonItemStyleBordered target:self action:@selector(backBtnAction)];
	
	[myToolBar setItems:[NSArray arrayWithObjects:button,nil]];    
	[myToolBar setBarStyle:UIBarStyleDefault];
	CGRect toolBarFrame;
	toolBarFrame = CGRectMake(0, applicationFrame.size.height-44, applicationFrame.size.width, 44);
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
        // EXAMPLE: just grab the first barcode
        break;
	
    // ADD: dismiss the controller
    [zBarReader dismissModalViewControllerAnimated: YES];

    NSLog(@"serial=%@",symbol.data);
    
    [self generateCheckIDRequest:symbol.data];
    
    // EXAMPLE: do something useful with the barcode image
    //resultImage.image = [info objectForKey: UIImagePickerControllerOriginalImage];
	
}


#pragma mark -
#pragma mark Memory management


- (void)viewDidLoad {
    [super viewDidLoad];
	[validationActivityIndicator stopAnimating];
			
	scrollView.hidden = YES;
	
	[scrollView setContentSize:CGSizeMake(320, 340)];
}


- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload {
    self.barcodeIDField = nil;
    self.statusTextView = nil;
	self.homeAddressTextView = nil;
	self.uniAddressTextView = nil;
    
    [super viewDidUnload];
}


- (void)dealloc {
	[barcodeIDField release];
	[validationActivityIndicator release];
	[resultLabel release];
	[barcodeIDLabel release];
	[nameLabel release];
	[statusTextView release];
	[homeAddressTextView release];
	[uniAddressTextView release];
	[super dealloc];
}


@end
