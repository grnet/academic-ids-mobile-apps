//
//  AcademicID_CheckView2Controller.m
//  AcademicID Check
//
//  Created by Michalis Masikos on 11/23/12.
//  Copyright 2012 __MyCompanyName__. All rights reserved.
//

#import "Offers_CheckView2Controller.h"
#import "Offers_CheckAppDelegate.h"
#import "YAJLiOS/YAJL.h"
#import "ScanOverlayViewController.h"

@implementation Offers_CheckView2Controller
@synthesize barcodeIDField;
@synthesize validationActivityIndicator;
@synthesize resultLabel;
@synthesize barcodeIDLabel;
@synthesize nameLabel;
@synthesize statusTextView;
@synthesize homeAddressTextView;
@synthesize uniAddressTextView;

-(IBAction)logout:(id)sender
{
	barcodeIDField.text = nil;
	scrollView.hidden = YES;
	
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	[appDelegate switchToView1:self.view];
	[appDelegate setAuthValue:nil];
	[appDelegate setUserName:nil];
	[appDelegate setPwdMD5Stored:nil];
}

-(IBAction)viewSettings:(id)sender
{
	
	Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
	[appDelegate switchToView3:self.view];
}

-(IBAction)checkID:(id)sender
{
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
	else 
	{
		[barcodeIDField resignFirstResponder];
		responseData = [[NSMutableData data] retain];
		NSString *urlString = [[NSString alloc] initWithFormat:@"http://academicidappbuilder.grnet.gr:8080/admin/web/ws/users/loadProviderOffers"];
		NSLog(@"urlString=%@",urlString);
		
		[self.view setUserInteractionEnabled:NO];  
		[validationActivityIndicator startAnimating];
		

		NSMutableURLRequest *mutableRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestReloadIgnoringCacheData
																  timeoutInterval:12];
		
		[urlString release];
		
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		NSString *authValue = [appDelegate authValue];
		
		
		[mutableRequest setHTTPMethod:@"GET"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"CONTENT-TYPE"];
		[mutableRequest setValue:@"application/json" forHTTPHeaderField:@"ACCEPTS"];
		[mutableRequest setValue:authValue forHTTPHeaderField:@"AUTHORIZATION"];
		
		NSLog(@"HTTP request headers: %@", [mutableRequest allHTTPHeaderFields]);
		
		
		[[NSURLConnection alloc] initWithRequest:mutableRequest delegate:self];		
	}
}


-(IBAction)backgroundTap:(id)sender { 
	[barcodeIDField resignFirstResponder];
}

-(IBAction)scanTap:(id)sender { 
	// ADD: present a barcode reader that scans from the camera feed
    //ZBarReaderViewController *
	reader = [ZBarReaderViewController new];
	//ScanOverlayViewController *overlay = [ScanOverlayViewController new];
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
	//[overlay release];
    [reader release];
	
}


-(UIView *)overlayView
{
		
	UIView *view=[[UIView alloc] initWithFrame:CGRectMake(0, 436, 320, 44)];
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
        // EXAMPLE: just grab the first barcode
        break;
	
    // EXAMPLE: do something useful with the barcode data
    //resultText.text = symbol.data;
	
    // EXAMPLE: do something useful with the barcode image
    //resultImage.image = [info objectForKey: UIImagePickerControllerOriginalImage];
	
    // ADD: dismiss the controller (NB dismiss from the *reader*!)
    [zBarReader dismissModalViewControllerAnimated: YES];
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
		Offers_CheckAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
		[appDelegate switchToView1:self.view];
		[appDelegate setAuthValue:nil];
		[appDelegate setUserName:nil];
		[appDelegate setPwdMD5Stored:nil];
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
	
	NSString *statusString = @"ΑΚΥΡΟ";
	NSString *statusDetailString = [resultsDict objectForKey:@"error"];
	[self.resultLabel setTextColor:[UIColor redColor]];
	
	if ([[resultsDict objectForKey:@"valid"] intValue] == TRUE)
	{
		statusString = @"ΕΓΚΥΡΟ";
		statusDetailString = @"-";
		[self.resultLabel setTextColor:[UIColor greenColor]];
	}
	
	[self.resultLabel setText:statusString];
	[self.barcodeIDLabel setText:[resultsDict objectForKey:@"academicId"]];
	[self.nameLabel setText:[resultsDict objectForKey:@"name"]];
	[self.statusTextView setText:statusDetailString];
	[self.homeAddressTextView setText:[resultsDict objectForKey:@"residenceLocation"]];
	[self.uniAddressTextView setText:[resultsDict objectForKey:@"universityLocation"]];
	
	[scrollView setContentOffset:CGPointZero animated:NO];
}


// The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
/*
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization.
    }
    return self;
}
*/


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
	[validationActivityIndicator stopAnimating];
			
	scrollView.hidden = YES;
	
	[scrollView setContentSize:CGSizeMake(320, 340)];
}


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations.
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc. that aren't in use.
}

- (void)viewDidUnload {
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
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
