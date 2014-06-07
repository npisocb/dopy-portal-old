// dependencies
var fs = require('fs');
var express = require('express');
var routes = require('./routes');
var path = require('path');
var config = require('./oauth.js');
var User = require('./user.js');
var mongoose = require('mongoose');
var passport = require('passport');
var auth = require('./authentication.js');

// connect to the database
mongoose.connect('mongodb://localhost/passport-example');

var app = express();

app.configure(function() {
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.logger());
  app.use(express.cookieParser());
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(express.session({ secret: 'my_precious' }));
  app.use(passport.initialize());
  app.use(passport.session());
  app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

// seralize and deseralize
passport.serializeUser(function(user, done) {
    console.log('serializeUser: ' + user._id)
    done(null, user._id);
});
passport.deserializeUser(function(id, done) {
    User.findById(id, function(err, user){
        console.log(user);
        if(!err) done(null, user);
        else done(err, null);
    })
});

// routes
app.get('/', routes.index);
app.get('/ping', routes.ping);
app.get('/account', ensureAuthenticated, function(req, res){
  User.findById(req.session.passport.user, function(err, user) {
    if(err) { 
      console.log("WARNING: Unauthorized Access Detected!"); 
    } else {
      res.render('account', { user: user});
    }
  })
})
app.get('/auth/facebook',
  passport.authenticate('facebook'),
  function(req, res){
  });
app.get('/auth/facebook/callback', 
  passport.authenticate('facebook', { failureRedirect: '/' }),
  function(req, res) {
    res.redirect('/account');
  });
app.get('/auth/twitter',
  passport.authenticate('twitter'),
  function(req, res){
  });
app.get('/auth/twitter/callback', 
  passport.authenticate('twitter', { failureRedirect: '/' }),
  function(req, res) {
    res.redirect('/account');
  });
app.get('/auth/github',
  passport.authenticate('github'),
  function(req, res){
  });
app.get('/auth/github/callback', 
  passport.authenticate('github', { failureRedirect: '/' }),
  function(req, res) {
    res.redirect('/account');
  });
app.get('/auth/google',
  passport.authenticate('google'),
  function(req, res){
  });
app.get('/auth/google/callback', 
  passport.authenticate('google', { failureRedirect: '/' }),
  function(req, res) {
    res.redirect('/account');
  });
app.get('/logout', function(req, res){
  req.logout();
  console.log("Logout Successful.");
  res.redirect('/');
});

//PASSPORT + UPLOADR TESTING REGION
//TREAD WITH CARE !!

var form = "<!DOCTYPE HTML><html><head><h1><center>DOPY UPLOADER</center></h1></head><body bgcolor='blue' textcolor='yellow'>" +
"<center>"+
"<form method='post' action='/upload' enctype='multipart/form-data'>" +
"<input type='file' name='image'/>" +
"<input type='submit' /></form>" +
"</center></body></html>";

app.get('/uploadr', function (req, res){
        res.writeHead(200, {'Content-Type': 'text/html' });
        res.end(form);
	console.log("Uploadr started! Unstable Form Loaded!");

});


app.post('/upload', function(req, res) {

        fs.readFile(req.files.image.path, function (err, data) {

                var imageName = req.files.image.name

                /// If there's an error
                if(!imageName){

                        console.log("There was an error")
                        res.redirect("/");
                        res.end();

                } else {

                  var newPath = __dirname + "/uploads/fullsize/" + imageName;

                  /// write file to uploads/fullsize folder
                  fs.writeFile(newPath, data, function (err) {

                        /// let's see it
                        res.redirect("/uploads/fullsize/" + imageName);

                  });
                }
        });
});


/// Show files
app.get('/uploads/fullsize/:file', function (req, res){
	file = req.params.file;
	var img = fs.readFileSync(__dirname + "/uploads/fullsize/" + file);
	res.writeHead(200, {'Content-Type': 'image/jpg' });
	res.end(img, 'binary');

});

app.get('/photos', function(req, res) {
	var dir = "./uploads/fullsize";
	var files = fs.readdirSync(dir);
    for(var i in files){
        if (!files.hasOwnProperty(i)) continue;
        var name = dir+'/'+files[i];
        if (fs.statSync(name).isDirectory()){
            getFiles(name);
        }else{
            console.log(name)
	    res.end(i)
        }
    }
});
//END OF UPLAODR V1. Lynch-eNVY Computer Corp.

// port
app.listen(1337);

// test authentication
function ensureAuthenticated(req, res, next) {
  if (req.isAuthenticated()) { return next(); }
  res.redirect('/');
}

module.exports = app
