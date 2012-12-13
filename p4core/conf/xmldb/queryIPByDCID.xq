declare namespace mets="http://www.loc.gov/METS/";
declare namespace dc="http://purl.org/dc/elements/1.1/";
declare variable $IDENTIFIER as xs:string external;
declare variable $COLLECTION as xs:string external;
for $mets in collection($COLLECTION)//mets:mets
where $mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:record/dc:identifier = $IDENTIFIER
return data($mets/@OBJID)