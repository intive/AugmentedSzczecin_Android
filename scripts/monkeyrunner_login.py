from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
import commands
import sys
import os
import string 
import random

def generateEmail():
	device.press('KEYCODE_W', 'DOWN_AND_UP')
	device.press('KEYCODE_I', 'DOWN_AND_UP')
	device.press('KEYCODE_T', 'DOWN_AND_UP')
	device.press('KEYCODE_A', 'DOWN_AND_UP')
	device.press('KEYCODE_A', 'DOWN_AND_UP')
	device.press('KEYCODE_J', 'DOWN_AND_UP')
	device.press('KEYCODE_AT','DOWN_AND_UP')
	device.press('KEYCODE_A', 'DOWN_AND_UP')
	device.press('KEYCODE_J', 'DOWN_AND_UP')
	device.press('KEYCODE_PERIOD', 'DOWN_AND_UP')
	device.press('KEYCODE_A', 'DOWN_AND_UP')
	device.press('KEYCODE_J', 'DOWN_AND_UP')
def generatePassword():
	device.press('KEYCODE_W', 'DOWN_AND_UP')
	device.press('KEYCODE_I', 'DOWN_AND_UP')
	device.press('KEYCODE_T', 'DOWN_AND_UP')
	device.press('KEYCODE_A', 'DOWN_AND_UP')
	device.press('KEYCODE_A', 'DOWN_AND_UP')
	device.press('KEYCODE_J', 'DOWN_AND_UP')
	device.press('KEYCODE_W', 'DOWN_AND_UP')
	device.press('KEYCODE_9', 'DOWN_AND_UP')
def randomEvents(width,height):
	print width
	print height
	print "Random touch"
	for i in range(0,10):
		MonkeyRunner.sleep(2)
		device.touch(random.uniform(0,float(width)), random.uniform(0,float(height)), 'DOWN_AND_UP')
		print "Touched"
print "Starting the monkeyrunner script"

device = MonkeyRunner.waitForConnection()
print "Installation"
path = os.path.dirname(sys.argv[0])
apkName = 'app-debug.apk';
device.installPackage(path + '\\..\\app\\build\\outputs\\apk\\' + apkName)
package = 'com.blstream.as'
activity = '.MainActivity'
runComponent = package + '/' + activity
device.startActivity(component=runComponent)
MonkeyRunner.sleep(10)
device.touch(240, 592, 'DOWN_AND_UP')
MonkeyRunner.sleep(2)
device.touch(239, 150, 'DOWN_AND_UP')
MonkeyRunner.sleep(2)
generateEmail()
device.touch(239, 230, 'DOWN_AND_UP')
generatePassword()
device.touch(245, 325, 'DOWN_AND_UP')
print "Sign in complete"

width = device.getProperty('display.width')
height = device.getProperty('display.height')
randomEvents(width,height)

MonkeyRunner.sleep(10)
device.touch(40, 80, 'DOWN_AND_UP')
MonkeyRunner.sleep(5)
device.touch(143, 800, 'DOWN_AND_UP')
print "Sign out complete"

print "Finishing the test" 