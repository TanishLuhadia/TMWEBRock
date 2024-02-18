class Student
{
}
class StudentService
{
add(formID) 
{
if(formID)
{
var rollNumber = $('#'+formID+' #rollNumber').val();
var name = $('#'+formID+' #name').val();
var gender = $('#'+formID+' #gender').val();
}
else
{
var rollNumber = $('#rollNumber').val();
var name = $('#name').val();
var gender = $('#gender').val();
}
var requestData = {
id: rollNumber,
name: name,
gender: gender
};
var url = '/TMWEBRock/studentService/student/add';
var prm = new Promise(function (done, problem) {
$.ajax({
url: url,
type: 'POST',  // Change the request type to POST
data: JSON.stringify(requestData),
contentType: 'application/json',
success: function (response) {
done(response);
},
error:function(response){
problem(response);
}
});
});
return prm;
}
update()
{
var rollNumber = document.getElementById('rollNumber').value;
var name=document.getElementById('name').value;
var gender=document.getElementById('gender').value;
var requestData = {
id: rollNumber, 
name: name,
gender:gender
};
var prm=new Promise(function(done,problem){
$.ajax({
url: '/TMWEBRock/studentService/student/update',
type: 'GET',
data: JSON.stringify(requestData), 
contentType: 'application/json', 
success: function (response) {
done(response);
},
error:function(response){
problem(response);
}
});
});
return prm;
}

idExists()
{
var prm=new Promise(function(done,problem){
$.ajax({
url: '/TMWEBRock/studentService/student/idExists',
type: 'GET',
success: function (response) {
done(response);
},
error:function(response){
problem(response);
}
});
});
return prm;
}

delete(formID)
{
var prm=new Promise(function(done,problem)
{
if(formID) 
{
var roll = $('#' + formID + ' #rollNumber').val();
alert("nikjn");
}
else {
var roll=document.getElementById("rollNumberr").value;
alert("bjk");
}
alert(roll);
var url=encodeURIComponent(roll);
$.ajax({
url: '/TMWEBRock/studentService/student/delete?id='+url,
type: 'GET',
success: function (response) {
done(response);
},
error:function(response){
problem(response);
}
});
});
return prm;
}


getById()
{
var prm=new Promise(function(done,problem){
$.ajax({
url: '/TMWEBRock/studentService/student/getById',
type: 'GET',
success: function (response) {
done(response);
},
error:function(response){
problem(response);
}
});
});
return prm;
}


getAll()
{
var prm=new Promise(function(done,problem){
$.ajax({
url: '/TMWEBRock/studentService/student/getAll',
type: 'GET',
success: function (response) {
done(response);
},
error:function(response){
problem(response);
}
});
});
return prm;
}
}

