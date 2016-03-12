WalkData= read.table( file="C:/Users/Rakesh/Desktop/BA project/ADL_Dataset/HMP_Dataset/Walk_MODEL/Accelerometer-2011-05-30-08-29-07-walk-f1_1.txt", header=FALSE, sep=" ", na.strings="NA", fill=TRUE, strip.white=TRUE, blank.lines.skip=TRUE, comment.char="", allowEscapes=FALSE, flush=FALSE, stringsAsFactors=TRUE)
getupdata= read.table( file="C:/Users/Rakesh/Desktop/BA project/ADL_Dataset/HMP_Dataset/Getup_bed_MODEL/Accelerometer-2011-03-29-09-24-50-getup_bed-f1.txt", header=FALSE, sep=" ", na.strings="NA", fill=TRUE, strip.white=TRUE, blank.lines.skip=TRUE, comment.char="", allowEscapes=FALSE, flush=FALSE, stringsAsFactors=TRUE)
climbdata= read.table( file="C:/Users/Rakesh/Desktop/BA project/ADL_Dataset/HMP_Dataset/Climb_stairs_MODEL/Accelerometer-2011-03-24-10-24-39-climb_stairs-f1.txt", header=FALSE, sep=" ", na.strings="NA", fill=TRUE, strip.white=TRUE, blank.lines.skip=TRUE, comment.char="", allowEscapes=FALSE, flush=FALSE, stringsAsFactors=TRUE)

#summary(getupdata)
#summary(climbdata)
#attributes(WalkData)
#colnames(WalkData)
test<-c(12,42,35)
walkdatameansd=as.data.frame( t(sapply(WalkData, function(cl) list(means=mean(cl,na.rm=TRUE),sds=sd(cl,na.rm=TRUE))) ))
walkdatameansd
lhvaluewalk<-1
for(i in 1:3)
{
  
  lhvaluewalk<-lhvaluewalk*dnorm(test[i],as.numeric(walkdatameansd[i,1]),as.numeric(walkdatameansd[i,2]))
}
getupdatameansd=as.data.frame( t(sapply(getupdata, function(cl) list(means=mean(cl,na.rm=TRUE),sds=sd(cl,na.rm=TRUE))) ))
getupdatameansd
lhvaluegetup<-1
for(i in 1:3)
{
  
  lhvaluegetup<-lhvaluegetup*dnorm(test[i],as.numeric(getupdatameansd[i,1]),as.numeric(getupdatameansd[i,2]))
}
climbdatameansd=as.data.frame( t(sapply(climbdata, function(cl) list(means=mean(cl,na.rm=TRUE),sds=sd(cl,na.rm=TRUE))) ))
climbdatameansd
lhvalueclimb<-1
for(i in 1:3)
{
  
  lhvalueclimb<-lhvalueclimb*dnorm(test[i],as.numeric(climbdatameansd[i,1]),as.numeric(climbdatameansd[i,2]))
}
probW=(nrow(WalkData)/(nrow(WalkData)*nrow(getupdata)*nrow(climbdata)))*lhvaluewalk
probG=(nrow(getupdata)/(nrow(WalkData)*nrow(getupdata)*nrow(climbdata)))*lhvaluegetup
probC=(nrow(climbdata)/(nrow(WalkData)*nrow(getupdata)*nrow(climbdata)))*lhvalueclimb
probW
probG
probC

maxprob=max(probC,probG,probW)
if(maxprob==probC){
  print("Climbing stairs")
}else if(maxprob==probG){
  print("Getting up bed")
}else{
  print("Walking")
}