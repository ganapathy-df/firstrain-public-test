#!/usr/bin/python
import urllib
import urllib2
import sys

healthcheck_url = "http://10.69.9.15:60243/standard/static/healthmonitor.jsp"
def CheckDnbBulkProcessor():
	try:

		response = urllib.urlopen(healthcheck_url)
		if(response.getcode() != 200):
			return -1
		return response.read().find("Response health OK.\n")
	except IOError:
		print ("url " + healthcheck_url + " not reacheable \n")
		return -1
	except:
		print("Unexpected Error:",sys.exc_info()[0])
		return -1



if __name__ == "__main__":
	success = CheckDnbBulkProcessor()
	if( success != -1):
		print ("frstandardstatic is installed succesfully")
	else: 
		print ("frstandardstatic Failed")
		sys.exit(1)
