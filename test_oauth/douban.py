#!/usr/bin/python
from pyoauth2 import Client

KEY = '<your_key>'
SECRET = '<your_secret>'
CALLBACK = '<your_callback>'

client = Client(KEY, SECRET,
                site='https://api.douban.com',
                authorize_url='https://www.douban.com/service/auth2/auth',
                token_url='https://www.douban.com/service/auth2/token')

print '-' * 80
authorize_url = client.auth_code.authorize_url(redirect_uri=CALLBACK, scope='shuo_basic_w,douban_basic_common')
print 'Go to the following link in your browser: '
print authorize_url

print '-' * 80
code = raw_input('Enter the verification code and hit ENTER when you\'re done: ')
code = code.strip()
access_token = client.auth_code.get_token(code, redirect_uri=CALLBACK)
print 'token', access_token.headers

print '-' * 80
print 'get @me info' 
ret = access_token.get('/v2/user/~me')
print ret.parsed

print '-' * 80
print 'post miniblog...'
img = open('<your_imagefile>', 'rb')
ret = access_token.post('/shuo/v2/statuses/', text='naive.........', files=dict(image=img))
img.close()
print ret.parsed
