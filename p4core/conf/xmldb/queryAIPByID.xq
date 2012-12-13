xquery version "1.0";
declare namespace mets="http://www.loc.gov/METS/";
declare variable $AIPID as xs:string external;
declare variable $COLLECTION as xs:string external;
for $mets in collection($COLLECTION)//mets:mets[@OBJID=$AIPID]
return $mets