//
//  Offers_CheckAppConstants.m
//  Academic ID Merchant
//
//  Created by Michalis Masikos on 27/03/2014.
//  Copyright 2014 __GRNET__. All rights reserved.
//

#import "Offers_CheckAppConstants.h"
#import <Foundation/Foundation.h>

#ifdef CONFIGURATION_DEBUG
	NSString* const appBaseURL = @"http://academicidappbuilder.grnet.gr:8080/admin/web/ws/users/";
#else
	NSString* const appBaseURL = @"http://academicidapp.grnet.gr:8080/admin/web/ws/users/";
#endif
