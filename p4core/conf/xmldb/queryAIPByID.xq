xquery version "1.0";
declare namespace mets="http://www.loc.gov/METS/";
declare variable $AIPID as xs:string external;
for $mets in //mets:mets[@OBJID=$AIPID]
return $mets