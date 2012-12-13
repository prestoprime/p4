declare namespace mets="http://www.loc.gov/METS/";
declare namespace dc="http://purl.org/dc/elements/1.1/";

declare variable $FROM_DATE as xs:string external;
declare variable $UNTIL_DATE as xs:string external;
declare variable $SET as xs:string external;
declare variable $COLLECTION as xs:string external;

for $mets in collection($COLLECTION)//mets:mets
where ((string-length($FROM_DATE)=0 or not($mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:record/dc:date) or xs:date($mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:record/dc:date) gt xs:date($FROM_DATE))
and (string-length($UNTIL_DATE)=0 or not($mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:record/dc:date) or xs:date($mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:record/dc:date) lt xs:date($UNTIL_DATE))
and (string-length($SET)=0 or contains(upper-case($mets/mets:metsHdr/@LABEL),upper-case($SET))))
return data($mets/@OBJID)