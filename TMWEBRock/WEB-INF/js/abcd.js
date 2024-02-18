class StudentService
{
update(formID)
{
if(formID)
{
var name=$('#'+formID+'  #name').val();
var id=$('#'+formID+'  #id').val();
var gender=$('#'+formID+'  #gender').val();
}
else
{
var name=$('#name').val();
var id=$('#id').val();
var gender=$('#gender').val();
}
var requestData={
name:name,
id:id,
gender:gender
};
var url='/TMWEBRock/studentService/student/update';
var prm=new Promise(function(done,problem){
$.ajax({
url:url,
type:'GET',
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
getAll(formID)
{
var prm=new Promise(function(done,problem){
$.ajax({
url:'/TMWEBRock/studentService/student/getAll',
type:'GET',
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
var roll = $('#' + formID + ' #id').val();
}
else
{
var roll=document.getElementById(id).value;
}
var url=encodeURIComponent(roll);
$.ajax({
url:'/TMWEBRock/studentService/student/delete?id='+url,
type:'GET',
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
getByID(formID)
{
var prm=new Promise(function(done,problem)
{
if(formID)
{
var roll = $('#' + formID + ' #id').val();
}
else
{
var roll=document.getElementById(id).value;
}
var url=encodeURIComponent(roll);
$.ajax({
url:'/TMWEBRock/studentService/student/getByID?id='+url,
type:'GET',
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
idExists(formID)
{
var prm=new Promise(function(done,problem)
{
if(formID)
{
var roll = $('#' + formID + ' #id').val();
}
else
{
var roll=document.getElementById(id).value;
}
var url=encodeURIComponent(roll);
$.ajax({
url:'/TMWEBRock/studentService/student/idExists?id='+url,
type:'GET',
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
add(formID)
{
if(formID)
{
var name=$('#'+formID+'  #name').val();
var id=$('#'+formID+'  #id').val();
var gender=$('#'+formID+'  #gender').val();
}
else
{
var name=$('#name').val();
var id=$('#id').val();
var gender=$('#gender').val();
}
var requestData={
name:name,
id:id,
gender:gender
};
var url='/TMWEBRock/studentService/student/add';
var prm=new Promise(function(done,problem){
$.ajax({
url:url,
type:'POST',
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
}