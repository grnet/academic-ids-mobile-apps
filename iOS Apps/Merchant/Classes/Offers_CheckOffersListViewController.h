//
//  Offers_CheckOffersListViewController.h
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 3/27/14.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Offers_CheckMainMenuViewController.h"
@class Offers_CheckOfferDetailsViewController;

@interface Offers_CheckOffersListViewController : UITableViewController {
	NSArray *list;
	Offers_CheckOfferDetailsViewController *childController;
}

@property (nonatomic, retain) NSArray *list;

-(NSString *) getDateStrFromJSONString:(NSString*)JSONdateStr;

@end
